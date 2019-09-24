

public class SourseStatement
{ 
    public String name;
    public String opcode;
    public String Mnemonic;
    public String content;
    public String objectcode;
    public String loc;
   
    public SourseStatement(String n,String mne,String con)
    {
    name=n;
	opcode="";
    Mnemonic=mne;
	content=con;
	objectcode="";
    loc="";
    }

    public SourseStatement(String mne,String con)
    {
    name="      ";
	opcode="";
    Mnemonic=mne;
	content=con;
	objectcode="";
    loc="";
    }

    public SourseStatement(String mne)
    {
    name="      ";
    opcode="";
    Mnemonic=mne;
    content="      ";
    objectcode="";
    loc="";
    }




}
