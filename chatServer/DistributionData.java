package chat;

/**
 * Created by runyyf on 2016-01-26.
 */
public class DistributionData {

    public String  sourceIp;
    public String  targetIp;
    public String  content;
    public Integer sendStatus;

    DistributionData(){
        sendStatus = 0 ;
        content  = "";
        sourceIp = "";
        targetIp = "";
    }

}
