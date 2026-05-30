package zeldaminiclone.resources;

import java.io.*;

public class Save {

    private static final String PATH = "resources/score/highscore.txt";

    public static void saveScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int loadScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATH))) {
            String line = reader.readLine();
            return Integer.parseInt(line);
        } catch (Exception e) {
            return 0;
        }
    }
}
