package programmer.model;

public class PacketResponse {

    boolean isHaveDesiredResponse;
    private byte[] packet;
    private int startingIndexOfAnswer;

    public int getStartingIndexOfAnswer() {
        return startingIndexOfAnswer;
    }

    public void setStartingIndexOfAnswer(int startingIndexOfAnswer) {
        this.startingIndexOfAnswer = startingIndexOfAnswer;
    }

    public boolean isHaveDesiredResponse() {
        return isHaveDesiredResponse;
    }

    public void setHaveDesiredResponse(boolean haveDesiredResponse) {
        isHaveDesiredResponse = haveDesiredResponse;
    }


    public byte[] getPacket() {
        return packet;
    }

    public void setPacket(byte[] packet) {
        this.packet = packet;
    }
}
