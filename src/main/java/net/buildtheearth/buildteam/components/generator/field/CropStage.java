package net.buildtheearth.buildteam.components.generator.field;

public enum CropStage {

    LOW("LOW", false),
    TALL("TALL", false),
    HARVESTED("HARVESTED", false),
    LIGHT("LIGHT", false),
    DARK("DARK", false),
    DRIED_OUT("DRIED OUT", true),
    OVERGROWN("OVERGROWN", true),
    DRY("DRY", false), //For harvested fields
    MUD("WET", false); //For harvested fields


    private final String identifier;
    private final boolean biomeRequired; //Indicates if the biome needs to be changed

    CropStage(String identifier, boolean biomeRequired) {
        this.identifier = identifier;
        this.biomeRequired = biomeRequired;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static CropStage getByIdentifier(String identifier) {
        for(CropStage cropStage : CropStage.values())
            if(cropStage.getIdentifier().equalsIgnoreCase(identifier))
                return cropStage;

        return null;
    }
}