module.exports = async ({ github, context, core }) => {
    const issue = context.payload.issue;
    const body = issue.body || "";
    const author = issue.user.login;
    const { owner, repo } = context.repo;

    const getField = (heading) => {
        const regex = new RegExp(`### ${heading}\\s+([^#\\r\\n]+)`, "i");
        const match = body.match(regex);
        const value = match ? match[1].trim() : null;
        return value && value !== "_No response_" ? value : null;
    };

    let modulo = getField("Qual módulo está associado\\?");
    if (modulo) modulo = modulo.split(" ")[0]; // "infra (docker, ...)" -> "infra"
    const prioridade = getField("Nível de prioridade");

    core.info(`Módulo detectado: ${modulo} | Prioridade: ${prioridade}`);

    const priorityColors = {
        "baixa": "0e8a16",
        "média": "fbca04",
        "alta": "d93f0b",
        "crítica": "b60205",
        "essencial": "2d3aef"
    };

    const moduleColor = "1d76db"

    const ensureLabel = async (name, color, description = "") => {
        try {
            await github.rest.issues.createLabel({
                owner,
                repo,
                name,
                color,
                description
            });
        } catch (error) {
            if (error.status !== 422) throw error;
        }
    }

    const labelsToAdd = [];

    if (modulo) {
        const labelModulo = `modulo:${modulo}`;
        await ensureLabel(labelModulo, moduleColor, `Issues relacionadas ao ${modulo}`);
        labelsToAdd.push(labelModulo);
    }

    if (prioridade) {
        const labelPrioridade = `prioridade:${prioridade}`;
        const color = priorityColors[prioridade] || "ededed";
        await ensureLabel(labelPrioridade, color, `Prioridade nível ${prioridade}`);
        labelsToAdd.push(labelPrioridade);
    }

    if (labelsToAdd.length > 0) {
        await github.rest.issues.addLabels({
            owner,
            repo,
            issue_number: issue.number,
            labels: labelsToAdd
        });
    }

    const tipoTarefa = getField("Qual o tipo da tarefa\\?");
    const currentLabels = issue.labels.map(l => l.name);

    let branchPrefix = "feat";
    if (tipoTarefa) {
        branchPrefix = tipoTarefa;
    } else if (currentLabels.includes("bug")) {
        branchPrefix = "fix";
    }

    const slug = (issue.title || "descricao-curta")
        .replace(/^\[.*?\]\s*/, "")
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, "-")
        .replace(/^-+|-+$/g, "")
        .slice(0, 35);

    const branchName = `${branchPrefix}/${issue.number}-${slug || "tarefa"}`;
    const branchFooter = `\n\n💡 **Sugestão de branch:**\n\`\`\`bash\ngit checkout -b ${branchName}\n\`\`\``;

    const botTag = "<!-- triage-bot -->";
    const { data: comments } = await github.rest.issues.listComments({
        owner, repo, issue_number: issue.number
    });
    const botComment = comments.find(c => c.body?.includes(botTag));

    let message = `${botTag}\nOlá @${author}! Já recebemos sua issue, seja bem vindo(a)`
    if (modulo && prioridade) {
        message = `${botTag}\nOlá @${author}! Recebemos sua issue e já classificamos como ${modulo}, e com prioridade ${prioridade}`

        try {
            await github.rest.issues.removeLabel({
                owner,
                repo,
                issue_number: issue.number,
                name: "needs_triage"
            });
        } catch (error) {
            if (error.status !== 404) throw error;
        }
    } else if (modulo) {
        message = `${botTag}\nOlá @${author}! Recebemos sua issue e já classificamos como ${modulo}`
    } else if (prioridade) {
        message = `${botTag}\nOlá @${author}! Recebemos sua issue e já classificamos com prioridade ${prioridade}`
    }

    message = message.concat(branchFooter)

    if (botComment) {
        await github.rest.issues.updateComment({
            owner, repo, comment_id: botComment.id, body: message
        });
    } else {
        await github.rest.issues.createComment({
            owner, repo, issue_number: issue.number, body: message
        });
    }
}