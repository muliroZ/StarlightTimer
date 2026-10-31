module.exports = async ({ github, context, core }) => {
    const pr = context.payload.pull_request;
    const title = pr.title || "";
    const body = pr.body || "";
    const { owner, repo } = context.repo
    const errors = [];

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

    if (errors.length > 0) {
        const errorMessage = [
            botTag,
            `### ⚠️ Pendências na Governança do PR`,
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
    } else if (botComment) {
        const successMessage = `${botTag}\n### ✅ Governança do PR Aprovada\nObrigado pelos ajustes, @${pr.user.login}! Todos os critérios do template foram atendidos.`;

        await github.rest.issues.updateComment({
            owner,
            repo,
            comment_id: botComment.id,
            body: successMessage
        });
    }
}