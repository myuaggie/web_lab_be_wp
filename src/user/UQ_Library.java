package user;

import java.util.Date;

public class UQ_Library {
    private ULKey ulKey;
    private Question question;
    private String tagOne;
    private String tagTwo;
    private Date date;
    private int frequency;

    public UQ_Library(){}

    public ULKey getUlKey(){return ulKey;}
    public void setUlKey(ULKey ulKey){this.ulKey=ulKey;}

    public Question getQuestion(){return question;}
    public void setQuestion(Question question){this.question=question;}

    public String getTagOne(){return tagOne;}
    public void setTagOne(String tagOne){this.tagOne=tagOne;}

    public String getTagTwo(){return tagTwo;}
    public void setTagTwo(String tagTwo){this.tagTwo=tagTwo;}

    public Date getDate(){return date;}
    public void setDate(Date date){this.date=date;}

    public int getFrequency(){return frequency;}
    public void setFrequency(int frequency){this.frequency=frequency;}
}
