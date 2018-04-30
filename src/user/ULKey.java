package user;

import java.io.Serializable;

public class ULKey implements Serializable {
    private int userId;
    private int libraryId;

    public ULKey(){}
    public ULKey(int userId,int libraryId){
        this.userId=userId;
        this.libraryId=libraryId;
    }

    public int getUserId(){return userId;}
    public void setUserId(int userId){this.userId=userId;}

    public int getLibraryId(){return libraryId;}
    public void setLibraryId(int libraryId){this.libraryId=libraryId;}

    @Override
    public boolean equals(Object o) {
        if (o instanceof ULKey) {
            ULKey ulk = (ULKey) o;
            if (this.userId == ulk.getUserId() && this.libraryId == ulk.getLibraryId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.userId*2+this.libraryId*4;
    }
}
