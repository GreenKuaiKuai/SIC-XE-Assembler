import java.io.*;
import java.util.ArrayList;

public class Assembler
{
    ArrayList<SourseStatement> myList = new ArrayList<SourseStatement>();
    int op_size=0;
    Op Z[]=new Op[70];

    public static void main(String[] args) throws IOException
    {
        Assembler demo=new Assembler();
        demo.pass_one();
        demo.loc();
        demo.read_opcode_file();
        demo.pass_two();
        demo.Object_Program();
    }

//pass1 把檔案讀進來
    public void pass_one() throws IOException
    {
        FileReader fr = new FileReader("Figure2.5.txt");
        BufferedReader br = new BufferedReader(fr);
        int count=0;
        String line,A="",B="",C="",dot=".";
        SourseStatement temp=new SourseStatement(A);

        while((line=br.readLine())!=null)  
        {
            String d1=line.trim(); //刪除註解
            d1=d1.substring(0,1);
            if(dot.equals(d1))
                continue;

            String[] aArray=line.trim().split("\\s+"); 
            for(String x:aArray)
            {
                ++count;
                switch(count)
                {
                    case 1:
                        A=x;
                        break;
                    case 2:
                        B=x;
                        break;
                    case 3:
                        C=x;
                        break;
                    default:
                        break;
                }
                if(count==3)
                    break;
            }

            if(count==1)
                temp=new SourseStatement(A);
            if(count==2)
                temp=new SourseStatement(A,B); 
            if(count==3)
                temp=new SourseStatement(A,B,C);

            myList.add(temp);
            count=0;
        }
    }

//編LOC
    public void loc()
    {
        myList.get(0).loc="0000";
        int D=0000,res; //從0000開始
        String LOC="";

        for(int i=1;i<myList.size()-1;i++)
        {
            LOC=Integer.toHexString(D).toUpperCase();
            while(LOC.length()<4)
            {
                LOC="0"+LOC;
            }
            myList.get(i).loc=LOC;

            if(myList.get(i).Mnemonic.equals("RESB")==true)
            {
                res=Integer.valueOf(myList.get(i).content);
                D=D+res;
            }
            else if(myList.get(i).Mnemonic.equals("RESW")==true)
            {
                res=Integer.valueOf(myList.get(i).content)*3;
                D=D+res;
            }
            else if(myList.get(i).Mnemonic.equals("BASE")==true)
            {
                myList.get(i).loc="";
            }
            else if(myList.get(i).Mnemonic.equals("BYTE")==true)
            {
                String CX=myList.get(i).content.substring(0,1);
                if(CX.equals("C")==true)
                    D=D+3;
                else if(CX.equals("X")==true)
                    D=D+1;
            }
            else if(myList.get(i).Mnemonic.substring(0,1).equals("+")) //format4
                D=D+4;
            else if(myList.get(i).content.length()==1&&is_number(myList.get(i).content)==false) //format2
                D=D+2;
            else if(myList.get(i).content.substring(1,2).equals(",")) //format2
                D=D+2;
            else
                D=D+3;
        }        
    }

//read opcode file
    public void read_opcode_file() throws IOException
    {
        String line1,E="",F="";
        int count1=0;
        FileReader fr1 = new FileReader("op.txt");
        BufferedReader br1 = new BufferedReader(fr1);
    
        while((line1=br1.readLine())!=null)
        {
            String[] bArray=line1.split("\\s+");
            for(String y:bArray)
            {
                ++count1;
                switch(count1)
                {
                    case 1:
                        E=y;
                        break;
                    case 2:
                        F=y;
                        break;
                }

            }
                Z[op_size]=new Op(E,F);
                //System.out.println(Z[op_size].name+"     "+Z[op_size].code);
            
                count1=0;
                op_size++; 
        }
    }

//pass 2
    public void pass_two()
    {
        print_list(0); //印第一行(START)
        int  n=0,i=0,x=0,b=0,p=0,e=0,N;
        String str="",num="",address="";

        for(int j=1;j<myList.size()-1;j++)
        {
            if(myList.get(j).Mnemonic.equals("BYTE")==true)
                myList.get(j).objectcode=myList.get(j).content.substring(2,myList.get(j).content.length()-1);

            else if(myList.get(j).Mnemonic.equals("RSUB")==true)
                myList.get(j).objectcode="4F0000";

            else if(myList.get(j).Mnemonic.equals("BASE")==true||myList.get(j).Mnemonic.equals("RESW")==true||myList.get(j).Mnemonic.equals("RESB")==true)
                myList.get(j).objectcode="";

            else
            {
                for(int l=0;l<op_size;l++)//找出opcode代碼
                {
                    if(myList.get(j).Mnemonic.substring(0,1).equals("+")) //format4
                    {
                        if(myList.get(j).Mnemonic.substring(1).equals(Z[l].name)==true)
                        {
                            myList.get(j).opcode=Z[l].code;
                            break;
                        }
                    }
                    else
                    {
                        if(myList.get(j).Mnemonic.equals(Z[l].name)==true)
                        {
                            myList.get(j).opcode=Z[l].code;
                            break;
                        }
                    }
                }
                    

                if(myList.get(j).Mnemonic.substring(0,1).equals("+")) //format4
                {
                    x=0;e=1;
                    if(myList.get(j).content.substring(0,1).equals("#"))
                    {
                        n=0;i=1;
                        str=myList.get(j).content.substring(1);

                        if(is_number(str)==true)//如果是常數
                        {
                            b=0;p=0;
                            N=Integer.valueOf(myList.get(j).content.substring(1));
                            num=Integer.toHexString(N);
                            while(num.length()<5)
                            {
                                num="0"+num;//補0
                            }
                            setOpcode(j,n,i);
                            myList.get(j).objectcode=myList.get(j).opcode+set_xbpe(x,b,p,e)+num;
                        }
                    }
                    else
                    {
                        n=1;i=1;b=0;p=0;
                        for(int k=0;k<myList.size();k++)
                            if(myList.get(j).content.equals(myList.get(k).name)){
                                address=myList.get(k).loc;
                                break;
                            }
                        while(address.length()<5)
                        {
                            address="0"+address;
                        }
                        setOpcode(j,n,i);
                        myList.get(j).objectcode=myList.get(j).opcode+set_xbpe(x,b,p,e)+address;
                    }
                }
                else if(myList.get(j).content.length()==1&&is_number(myList.get(j).content)==false) //format2
                    myList.get(j).objectcode=myList.get(j).opcode+set_register(myList.get(j).content)+"0";
                else if(myList.get(j).Mnemonic.equals("COMPR")) //format2
                    myList.get(j).objectcode=myList.get(j).opcode+set_register(myList.get(j).content.substring(0,1))+set_register(myList.get(j).content.substring(2));
                else//format3
                {
                    e=0;
                    if(myList.get(j).content.substring(0,1).equals("#"))
                    {
                        n=0;i=1;x=0;
                        str=myList.get(j).content.substring(1);

                        if(is_number(str)==true)//如果是常數
                        {
                            b=0;p=0;
                            N=Integer.valueOf(myList.get(j).content.substring(1));
                            num=Integer.toHexString(N);
                            while(num.length()<3)
                            {
                                num="0"+num;//補0
                            }
                            setOpcode(j,n,i);
                            myList.get(j).objectcode=myList.get(j).opcode+set_xbpe(x,b,p,e)+num;
                        }
                        else//如果是變數
                        {
                            b=0;p=1;
                            setOpcode(j,n,i);
                            myList.get(j).objectcode=myList.get(j).opcode+set_xbpe(x,b,p,e)+disp(j,myList.get(j).content.substring(1));
                        }
                    }
                    else if(myList.get(j).content.substring(0,1).equals("@"))
                    {
                        n=1;i=0;x=0;b=0;p=1;
                        setOpcode(j,n,i);
                        myList.get(j).objectcode=myList.get(j).opcode+set_xbpe(x,b,p,e)+disp(j,myList.get(j).content.substring(1));
                    }
                    else if(myList.get(j).Mnemonic.equals("STCH")||myList.get(j).Mnemonic.equals("LDCH"))
                    {
                        n=1;i=1;x=1;b=1;p=1;
                        setOpcode(j,n,i);
                        myList.get(j).objectcode=myList.get(j).opcode+set_xbpe(x,b,p,e)+"003";
                    }
                    else
                    {
                        n=1;i=1;x=0;b=0;p=1;
                        setOpcode(j,n,i);
                        myList.get(j).objectcode=myList.get(j).opcode+set_xbpe(x,b,p,e)+disp(j,myList.get(j).content);
                    }
                }
            }
            print_list(j);
        }

        //印最後一行
        String space=" ";
        System.out.printf("%-8s",space);
        System.out.printf("%-8s",space);
        System.out.printf("%-8s",myList.get(myList.size()-1).Mnemonic);
        System.out.printf("%-10s",myList.get(myList.size()-1).content);
    }

//列印資料
    public void print_list(int j)
    {
        System.out.printf("%-8s",myList.get(j).loc);
        System.out.printf("%-8s",myList.get(j).name);
        System.out.printf("%-8s",myList.get(j).Mnemonic);
        System.out.printf("%-10s",myList.get(j).content);
        System.out.printf("%-10s\n",myList.get(j).objectcode.toUpperCase());
    }

//判斷常數
    public boolean is_number(String str)
    {
        for(int i=0;i<str.length();i++)
        {
            int chr=str.charAt(i);
            if(chr<48 || chr>57)
                return false;
        }
        return true;
    }

//相減
    public String disp(int j,String TA)
    {
        int front=0,target=0;
        String disp="";

        if(myList.get(j+1).loc.equals(""))
            front=Integer.parseInt(myList.get(j+2).loc,16);
        else
            front=Integer.parseInt(myList.get(j+1).loc,16);

        for(int i=0;i<myList.size();i++)
            if(TA.equals(myList.get(i).name))
            {
                target=Integer.parseInt(myList.get(i).loc,16);
                break;
            }
        if(target>front)
        {
            disp=Integer.toHexString(target-front);
            if(disp.length()==2)
                return "0"+disp;
            else if(disp.length()==1)
                return "00"+disp;
            else
                return " ";
        }
        else
        {
            front=~front+1; //取2的補數
            disp=Integer.toHexString(target+front);
            return disp.substring(disp.length()-3);
        }
    }

//修正opcode
    public void setOpcode(int j,int n,int i)
    {
        int m=Integer.parseInt(myList.get(j).opcode.substring(1),16);//取出opcode的第二位數並轉成16進位
        if(n==1)
            m=m+2;
        if(i==1)
            m=m+1;
        myList.get(j).opcode=myList.get(j).opcode.substring(0,1)+Integer.toHexString(m).toUpperCase();//設定修正後的opcode
    }

//設定xbpe
    public String set_xbpe(int x,int b,int p,int e)
    {
        int m=0;
        if(x==1)
            m=m+8;
        if(b==1)
            m=m+4;
        if(p==1)
            m=m+2;
        if(e==1)
            m=m+1;
        return Integer.toHexString(m);
    }

//設定暫存器代碼
    public String set_register(String r)
    {
        if(r.equals("X"))
            return "1";
        else if(r.equals("A"))
            return "0";
        else if(r.equals("S"))
            return "4";
        else if(r.equals("T"))
            return "5";
        else
            return " ";
    }

    public void Object_Program()
    {
        System.out.print("\n\n");
        String temp="";
        int stop=0,i=0,last=1,size=0,length=0;
        //H
        System.out.print("H");
        System.out.printf(".%-6s",myList.get(0).name);//程式名字
        System.out.print("."+to_six(myList.get(0).loc));//印起始位置
        temp=String.valueOf(Integer.valueOf(myList.get(myList.size()-2).loc)+1);
        System.out.println("."+to_six(temp));//程式大小

        //T
        while(stop==0)
        {
            System.out.print("T");
            System.out.print("."+to_six(myList.get(last).loc));

            /*for(i=last;i<myList.size();i++)//列印開始位置
                if(myList.get(i).objectcode.equals("")==false)//如果有objectcode
                {
                    System.out.print("."+to_six(myList.get(i).loc));
                    last=i;
                    break;
                }*/

            for(i=last;i<myList.size()-1;i++)//整理objectcode   
            { 
                if(myList.get(i).objectcode.equals("")==false)//如果有objectcode
                {
                    if(i==myList.size()-2)//最後一個
                    stop=1;

                    length=length+myList.get(i).objectcode.length();
                    if(length<60)
                    {
                        temp=temp+"."+myList.get(i).objectcode.toUpperCase();

                        switch(myList.get(i).objectcode.length())
                        {
                            case 4: //format2
                            size=size+2;
                            break;

                            case 6: //format3
                            size=size+3;
                            break;

                            case 8: //format4
                            size=size+4;
                            break;
                        }
                    }
                    else
                    {
                        length=length-myList.get(i).objectcode.length();
                        last=i;
                        break;
                    }
                }
            }
            System.out.print("."+Integer.toHexString(size).toUpperCase());
            System.out.println(temp);
            temp="";size=0;length=0;
        }

        //M
        for(i=0;i<myList.size();i++)
        {
            if(myList.get(i).Mnemonic.substring(0,1).equals("+"))//format4
            {
                System.out.print("M");
                temp=Integer.toHexString(Integer.parseInt(myList.get(i).loc,16)+1);
                System.out.println("."+to_six(temp).toUpperCase()+".05");
            }
        }

        //E
        System.out.print("E");
        System.out.print("."+to_six(myList.get(0).loc));

    }

//增加字串長度到6
    public String to_six(String str)
    {
        while(str.length()<6)
        {
            str="0"+str;
        }
        return str;
    }
  }


