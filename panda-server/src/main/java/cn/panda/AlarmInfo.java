package cn.panda;

import cn.panda.consumer.AlarmType;

public class AlarmInfo {
    private String id;
    private String extraInfo;
    public final AlarmType type;

    public AlarmInfo(String id, AlarmType type) {
        this.id = id;
        this.type = type;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public String toString() {
        return "AlarmInfo [type=" + type + ",id=" + id + ", extraInfo=["
                + extraInfo + "]]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((extraInfo == null) ? 0 : extraInfo.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AlarmInfo other = (AlarmInfo) obj;
        if (extraInfo == null) {
            if (other.extraInfo != null)
                return false;
        } else if (!extraInfo.equals(other.extraInfo))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
