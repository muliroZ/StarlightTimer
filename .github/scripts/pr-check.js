module.exports = async ({ github, context, core }) => {
    const pr = context.payload.pull_request;
    const title = pr.title || "";
    const body = pr.body || "";
    let baseBranch = pr.base.ref;
    const headBranch = pr.head.ref;
    const { owner, repo } = context.repo
    const errors = [];

    let autoRedirectedToDev = false;

    if (headBranch !== "dev" && baseBranch !== "dev") {
        try {
            await github.rest.pulls.update({
                owner,
                repo,
                pull_number: pr.number,
                base: "dev"
            });
            baseBranch = "dev";
            autoRedirectedToDev = true;
            core.info(`🔄 Branch de destino alterada automaticamente de '${pr.base.ref}' para 'dev'.`);
        } catch (error) {
            errors.push(`❌ **Fluxo de branch inválido:** A branch \`${baseBranch}\` não aceita PRs diretos de \`${headBranch}\`. Tentamos redirecionar automaticamente para \`dev\`, mas ocorreu um erro: ${error.message}`);
        }
    }

    if (headBranch !== "dev") {
        const conventionalRegex = /^(feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert)(\([a-z0-9_-]+\))?!?: .+/i;
        if (!conventionalRegex.test(title)) {
            errors.push("❌ **Título fora do padrão:** Use Conventional Commits (ex: `feat: ...`, `fix(backend): ...`).");
        }

        const testSectionMatch = body.match(/## Como foi testado:([\s\S]*?)(?=##|$)/i);
        const testSection = testSectionMatch ? testSectionMatch[1] : "";
        const hasCheckedTest = /-\s*\[x\]/i.test(testSection);

        if (!hasCheckedTest) {
            errors.push("❌ **Testes não informados:** Marque pelo menos um checkbox (`- [x]`) na seção `## Como foi testado:`.");
        }
    }

    if (!pr.assignees || pr.assignees.length === 0) {
        await github.rest.issues.addAssignees({
            owner,
            repo,
            issue_number: pr.number,
            assignees: [pr.user.login]
        });
        core.info(`👤 Autor @${pr.user.login} atribuído ao PR.`);
    }

    const issueMatches = [...body.matchAll(/(?:closes|fixes|resolves)\s+#(\d+)/gi)]
    const linkedIssueNumbers = issueMatches.map(m => Number(m[1]));

    const inheritedLabels = new Set();

    for (const issueNum of linkedIssueNumbers) {
        try {
            const { data: issueLabels } = await github.rest.issues.listLabelsOnIssue({
                owner,
                repo,
                issue_number: issueNum
            });

            for (const label of issueLabels) {
                if (label.name !== "needs_triage") {
                    inheritedLabels.add(label.name);
                }
            }
        } catch (error) {
            core.warning(`Não foi possível buscar a issue #${issueNum}: ${error.message}`)
        }
    }

    if (inheritedLabels.size > 0) {
        await github.rest.issues.addLabels({
            owner,
            repo,
            issue_number: pr.number,
            labels: [...inheritedLabels]
        });
        core.info(`🏷️ Labels herdadas da issue: ${[...inheritedLabels].join(", ")}`);
    }

    const botTag = "<!-- pr-check bot -->";
    const { data: comments } = await github.rest.issues.listComments({
        owner,
        repo,
        issue_number: pr.number
    });
    const botComment = comments.find(c => c.body?.includes(botTag));

    const redirectNotice = autoRedirectedToDev
        ? `\n> 🔄 **Nota:** Como esta é uma branch de trabalho (\`${headBranch}\`), alteramos automaticamente a branch de destino de (\`${pr.base.ref}\`) para \`dev\`.\n`
        : "";

    if (errors.length > 0) {
        const errorMessage = [
            botTag,
            `### ⚠️ Pendências na Governança do PR`,
            redirectNotice,
            `Olá @${pr.user.login}, encontramos alguns pontos que precisam de ajuste antes da revisão:`,
            "",
            ...errors,
            "",
            `_Edite a descrição ou o título deste PR para reprocessarmos automaticamente._`
        ].join("\n");

        if (botComment) {
            await github.rest.issues.updateComment({
                owner,
                repo,
                comment_id: botComment.id,
                body: errorMessage
            });
        } else {
            await github.rest.issues.createComment({
                owner,
                repo,
                issue_number: pr.number,
                body: errorMessage
            });
        }

        core.setFailed(`Foram encontrados problemas na governança do PR:\n${errors.join("\n")}`);
    } else if (botComment || autoRedirectedToDev) {
        const successMessage = `${botTag}\n### ✅ Governança do PR Aprovada\n${redirectNotice}Obrigado pelos ajustes, @${pr.user.login}! Todos os critérios do template foram atendidos.`;

        if (botComment) {
            await github.rest.issues.updateComment({
                owner,
                repo,
                comment_id: botComment.id,
                body: successMessage
            });
        } else {
            await github.rest.issues.createComment({
                owner,
                repo,
                issue_number: pr.number,
                body: successMessage
            });
        }
    }
}