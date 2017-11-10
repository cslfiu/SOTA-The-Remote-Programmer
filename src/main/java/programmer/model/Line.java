package programmer.model;

/**
 * Created by Burak on 10/23/17.
 */


/**
 * Created by Burak on 12/25/16.
 */
public class Line {

    private int size;
    private int address;
    private int type;
    private int next_address;
    private int checksum;
    private String data;
    private boolean isValid;

    public Line (String line) throws IllegalArgumentException
    {
        isValid = false;
        if(!line.startsWith(":"))
        {
            isValid = false;
            throw new IllegalArgumentException("Expected Argument can not start without ':' character ");

        }
        else
        {
            size = Integer.parseInt(line.substring(1,3),16);
            address = Integer.parseInt(line.substring(3,7),16);
            type = Integer.parseInt(line.substring(7,9),16);
            next_address = (9 + size *2);
            data = convertHexToString(line.substring(9,next_address));
            checksum = Integer.parseInt(line.substring(next_address,line.length()),16);
            isValid = Check();
        }
    }

    // Retrieved from https://www.mkyong.com/java/how-to-convert-hex-to-ascii-in-java/
    private String convertHexToString(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        for( int i=0; i<hex.length()-1; i+=2 ){
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char)decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }


    // Simple checksum
    private boolean Check()
    {
        int sum = size + (address >> 8) + (address & 0xFF) + type;

        for(char c: data.toCharArray())
        {
            sum += (int)c;
        }

        if(((~(sum & 0xFF) + 1) & 0xFF) == checksum)
        {
            //System.out.println("Yeah this true");
            return true;
        }
        return false;
    }

    public static void main(String[] args)
    {
        Line line = new Line(":1000000007C1000035C1000033C1000031C100004C");
        System.out.println(line.isValid());
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
