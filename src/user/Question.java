package user;

public class Question {
    private int id;
    private String name;
    private String content;
    private String reference;
    private User owner;

    public Question(){};
    public int getId(){return id;}
    public void setId(int id){this.id=id;}

    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    public String getContent(){return content;}
    public void setContent(String content){this.content=content;}

    public String getReference(){return reference;}
    public void setReference(String reference){this.reference=reference;}

    public User getOwner(){return owner;}
    public void setOwner(User owner){this.owner=owner;}

}
