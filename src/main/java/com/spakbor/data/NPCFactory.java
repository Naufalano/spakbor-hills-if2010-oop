package data;
import cls.core.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCFactory {
    private Map<String, NPC> npcMap = new HashMap<>();

    public NPCFactory(){
        npcMap.put("Emily", createNPC("Emily"));
        npcMap.put("Dasco", createNPC("Dasco"));
        npcMap.put("Perry", createNPC("Perry"));
        npcMap.put("Caroline", createNPC("Caroline"));
        npcMap.put("MayorTadi", createNPC("MayorTadi"));
        npcMap.put("Abigail", createNPC("Abigail"));
    }

    private NPC createNPC(String name){
        NPC npc;
        switch (name) {
            case "Emily":
                npc = new NPC(
                    "Emily", 
                    List.of(),
                    List.of("Catfish", "Salmon", "Sardine"),
                    List.of("Coal", "Wood")
                ); return npc;

            case "Dasco":
                npc = new NPC(
                    "Dasco", 
                    List.of("The Legends of Spakbor", "Cooked Pig's Head", "Wine", "Fugu", "Spakbor Salad"),
                    List.of("Fish Sandwich", "Fish Stew", "Baguette", "Fish n' Chips"),
                    List.of("Legend", "Grape", "Cauliflower", "Wheat", "Pufferfish", "Salmon")
                ); return npc;
            
            case "Perry":
                npc = new NPC(
                    "Perry", 
                    List.of("Cranberry", "Blueberry"),
                    List.of("Wine"),
                    List.of()
                ); return npc;
            
            case "Caroline":
                npc = new NPC(
                    "Caroline", 
                    List.of("Firewood", "Coal"),
                    List.of("Potato", "Wheat"),
                    List.of("Hot Pepper")
                ); return npc;

            case "MayorTadi":
                npc = new NPC(
                    "MayorTadi", 
                    List.of("Legend"),
                    List.of("Angler", "Crimsonfish", "Glacierfish"),
                    List.of()
                ); return npc;

            case "Abigail":
                npc = new NPC(
                    "Abigail", 
                    List.of("Blueberry", "Melon", "Grape", "Pumpkin", "Cranberry"),
                    List.of("Baguette", "Pumpkin Pie", "Wine"),
                    List.of("Hot Pepper", "Cauliflower", "Parsnip", "Wheat")
                ); return npc;

            default:
                return null;
        }
    }

    public NPC getNPC(String name){
        return this.npcMap.get(name);
    }

    public List<NPC> getAllNPCs() {
        return new ArrayList<>(npcMap.values());
    }
}
