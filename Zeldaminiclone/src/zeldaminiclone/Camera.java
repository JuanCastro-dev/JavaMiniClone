package zeldaminiclone;

public class Camera {

    public static int x;
    public static int y;

    //método que limita o movimento da câmera, para não mostrar fora do mapa
    public static int clamp(int atual, int minimo, int maximo) {
        if (atual < minimo) return minimo;
        if (atual > maximo) return maximo;
        return atual;
    }
}
