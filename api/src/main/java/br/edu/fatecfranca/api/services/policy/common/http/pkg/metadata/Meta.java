package br.edu.fatecfranca.api.services.policy.common.http.pkg.metadata;

/**
 * Metadados adicionais da resposta.
 *
 * Atualmente suporta informações de paginação.
 * Pode ser expandido futuramente sem alterar o payload principal.
 */
public final class Meta {

    private final PageMeta page;

    private Meta(PageMeta page) {
        this.page = page;
    }

    /**
     * Cria metadados de paginação.
     */
    public static Meta page(PageMeta page) {
        return new Meta(page);
    }

    public PageMeta getPage() {
        return page;
    }
}