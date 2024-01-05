import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final boolean OPEN = true;
    private static final boolean CLOSE = false;
    private int openSiteCount = 0;
    private final int size;
    private final WeightedQuickUnionUF site;
    private final boolean[][] grid;
    private final int topSiteIndex;
    private final int bottomSiteIndex;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        this.size = n;
        // +2 for the sites to be connected to sites in top row and bottom row
        int totalSites = n * n + 2;
        this.site = new WeightedQuickUnionUF(totalSites);
        this.topSiteIndex = totalSites - 2;
        this.bottomSiteIndex = totalSites - 1;
        this.grid = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = CLOSE;
            }
        }
        // union sites in top row to the same site
        // union sites in the bottom row to the same site
        int bottomRowStartIndex = n * (n - 1);
        for (int i = 0; i < this.size; i++) {
            this.site.union(i, this.topSiteIndex);
            if(n >= 2) {
                this.site.union(bottomRowStartIndex + i, this.bottomSiteIndex);
            }
        }

    }

    private int toIndex(int row, int column) {
        return (row - 1) * this.size + (column - 1);
    }

    /**
     * row & col provide arguments should be the real coordinate
     */
    private void connectWithNeighbors(int row, int col) {
        if (isOpen(row, col)) {
            int siteIndex = toIndex(row, col);
            int[][] neighbors = {{row + 1, col}, {row - 1, col}, {row, col + 1}, {row, col - 1}};
            for (int[] neighbor : neighbors) {
                int r = neighbor[0];
                int c = neighbor[1];
                if (isValidCoordinate(r, c) && isOpen(r, c)) {
                    this.site.union(siteIndex, toIndex(r, c));
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
        return isOpen(row, col) && this.site.find(toIndex(row, col)) == this.site.find(this.topSiteIndex);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return this.openSiteCount;
    }

    // does the system percolate?
    public boolean percolates() {
        return this.site.find(this.bottomSiteIndex) == this.site.find(this.topSiteIndex);
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation p = new Percolation(1);
        System.out.println(p.percolates());
    }
}