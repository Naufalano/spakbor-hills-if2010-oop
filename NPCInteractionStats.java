// Diasumsikan NPC.java sudah ada
public class NPCInteractionStats {
    private String npcName;
    private String currentRelationshipStatus; // Diambil dari NPC.getStatus()
    private int chatFrequency;
    private int giftFrequency;
    // private int visitFrequency; // "Visiting Frequency" perlu aksi "VisitNPC" yang jelas

    public NPCInteractionStats(String npcName) {
        this.npcName = npcName;
        this.currentRelationshipStatus = "Single"; // Status awal
        this.chatFrequency = 0;
        this.giftFrequency = 0;
        // this.visitFrequency = 0;
    }

    public String getNpcName() {
        return npcName;
    }

    public String getCurrentRelationshipStatus() {
        return currentRelationshipStatus;
    }

    public void setCurrentRelationshipStatus(String status) {
        this.currentRelationshipStatus = status;
    }

    public int getChatFrequency() {
        return chatFrequency;
    }

    public void incrementChatFrequency() {
        this.chatFrequency++;
    }

    public int getGiftFrequency() {
        return giftFrequency;
    }

    public void incrementGiftFrequency() {
        this.giftFrequency++;
    }

    // public int getVisitFrequency() { return visitFrequency; }
    // public void incrementVisitFrequency() { this.visitFrequency++; }

    @Override
    public String toString() {
        return "NPC: " + npcName +
               ", Status Hubungan: " + currentRelationshipStatus +
               ", Frekuensi Chat: " + chatFrequency +
               ", Frekuensi Hadiah: " + giftFrequency;
               // + ", Frekuensi Kunjungan: " + visitFrequency;
    }
}
