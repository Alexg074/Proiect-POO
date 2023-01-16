public final class Filter {
    private Sort sort;
    private Contains contains;

    Filter() { }

    public Sort getSort() {
        return sort;
    }

    public void setSort(final Sort sort) {
        this.sort = sort;
    }

    public Contains getContains() {
        return contains;
    }

    public void setContains(final Contains contains) {
        this.contains = contains;
    }
}
