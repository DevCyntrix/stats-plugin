package nl.lolmewn.stats.player;

public class StatMetaData {

    private final String id;
    private final Class<?> dataType;
    private final boolean groupable;

    public StatMetaData(String id, Class<?> dataType, boolean groupable) {
        this.id = id;
        this.dataType = dataType;
        this.groupable = groupable;
    }

    public String getId() {
        return id;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public boolean isGroupable() {
        return groupable;
    }
}
