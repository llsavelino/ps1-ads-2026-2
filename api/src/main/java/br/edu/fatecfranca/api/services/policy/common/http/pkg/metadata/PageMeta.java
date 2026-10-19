package br.edu.fatecfranca.api.services.policy.common.http.pkg.metadata;

/**
 * Metadados de paginação.
 */
public final class PageMeta {

    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PageMeta(
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "page não pode ser negativo."
            );
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "size deve ser maior que zero."
            );
        }

        if (totalElements < 0) {
            throw new IllegalArgumentException(
                    "totalElements não pode ser negativo."
            );
        }

        if (totalPages < 0) {
            throw new IllegalArgumentException(
                    "totalPages não pode ser negativo."
            );
        }

        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public static PageMeta of(
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        return new PageMeta(
                page,
                size,
                totalElements,
                totalPages
        );
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isFirst() {
        return page == 0;
    }

    public boolean isLast() {
        return totalPages == 0 || page >= totalPages - 1;
    }

    public boolean hasNext() {
        return !isLast();
    }

    public boolean hasPrevious() {
        return page > 0;
    }
}