public class Triangle {
    public static void drawTriangle(int size) {
        int col = 0;
        while (col < size) {
            int row = 0;
            col += 1;
            while (row < col) {
                System.out.print("*");
                row += 1;
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        drawTriangle(10);
    }
}