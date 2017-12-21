package CustomAutoNumber;

public class test {
    public static void main(String[] args) {
        String lol = "~abc123";
        String b = lol.replaceAll
                ("[0-9]", "");
        String c = b.replace("~","");
        System.out.println(c);

    }
}
