import edu.princeton.cs.algs4.WeightedQuickUnionUF;


public class Percolation {
    private static final boolean OPEN = true;
    private int openSiteCount = 0;
    private final int size;
    private final WeightedQuickUnionUF site;
    private final WeightedQuickUnionUF siteOnlyTop;
    private final boolean[][] grid;
    private final int virtualTop;
    private final int virtualBottom;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        this.size = n;
        // +2 for the sites to be connected to sites in top row and bottom row
        int totalSites = n * n;
        this.site = new WeightedQuickUnionUF(totalSites + 2);
        this.siteOnlyTop = new WeightedQuickUnionUF(totalSites + 1);
        this.virtualTop = totalSites;
        this.virtualBottom = totalSites + 1;
        this.grid = new boolean[n][n];
    }

    private int toIndex(int row, int column) {
        return (row - 1) * this.size + (column - 1);
    }

    private boolean isTopRowSite(int row) {
        return row == 1;
    }

    private boolean isBottomRowSite(int row) {
        return row == this.size;
    }

    /**
     * row & col provide arguments should be the real coordinate
     */
    private void connectWithNeighbors(int row, int col) {
        if (isOpen(row, col)) {
            int index = toIndex(row, col);
            if (isTopRowSite(row)) {
                this.site.union(index, this.virtualTop);
                this.siteOnlyTop.union(index, this.virtualTop);
            }
            if (isBottomRowSite(row)) {
                this.site.union(index, this.virtualBottom);
            }
            int[][] neighbors = {{row + 1, col}, {row - 1, col}, {row, col + 1}, {row, col - 1}};
            for (int[] neighbor : neighbors) {
                int r = neighbor[0];
                int c = neighbor[1];
                if (isValidCoordinate(r, c) && isOpen(r, c)) {
                    this.site.union(index, toIndex(r, c));
                    this.siteOnlyTop.union(index, toIndex(r, c));
                }
            }
        }
    }

    private boolean isValidCoordinate(int row, int col) {
        return !(row < 1 || col < 1 || row > this.size || col > this.size);
    }

    private void panicIfInvalid(int row, int col) {
        if (!isValidCoordinate(row, col)) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
    }

    private void openGrid(int row, int col) {
        grid[row - 1][col - 1] = OPEN;
        openSiteCount++;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        panicIfInvalid(row, col);
        if (!isOpen(row, col)) {
            openGrid(row, col);
            connectWithNeighbors(row, col);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        panicIfInvalid(row, col);
        return grid[row - 1][col - 1] == OPEN;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        panicIfInvalid(row, col);
        return this.siteOnlyTop.find(this.virtualTop) == this.siteOnlyTop.find(this.toIndex(row, col));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return this.openSiteCount;
    }

    // does the system percolate?
    public boolean percolates() {
        return this.site.find(this.virtualTop) == this.site.find(this.virtualBottom);
    }

    // test client (optional)
    public static void main(String[] args) {
        int n = 3;
        Percolation p = new Percolation(n);
        p.open(3, 1);
        p.open(1, 1);
        p.open(2, 1);
        System.out.println(p.isFull(1, 1));
        System.out.println(p.percolates());

    }
}