package user;

import java.io.Serializable;

public class URKey implements Serializable {
    private int userId;
    private int libraryId;
    private int recordId;

    public URKey(){}
    public URKey(int userId,int libraryId,int recordId){
        this.userId=userId;
        this.libraryId=libraryId;
        this.recordId=recordId;
    }

    public int getUserId(){ return userId; }
    public void setUserId(int userId){ this.userId=userId; }

    public int getLibraryId(){ return libraryId; }
    public void setLibraryId(int libraryId){ this.libraryId=libraryId; }

    public int getRecordId(){ return recordId; }
    public void setRecordId(int recordId){ this.recordId=recordId; }

    @Override
    public boolean equals(Object o) {
        if (o instanceof URKey) {
            URKey urk = (URKey) o;
            if (this.userId == urk.getUserId()
                    && this.libraryId == urk.getLibraryId()
                    && this.recordId == urk.getRecordId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.userId*2+this.libraryId*4+this.recordId*8;
    }


}
