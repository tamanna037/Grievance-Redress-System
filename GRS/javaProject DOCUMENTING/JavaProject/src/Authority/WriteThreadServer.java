package Authority;

/**
 * Created by MiNNiE on 12/15/2015.
 */

import NetworkRelatedClass.NetworkUtil;

import java.util.ArrayList;

/**
 * Server write each data object in stream in a new thread through this class.
 * this class needs the socket reference of the client to whom server wants to write
 * and the object or array list which is to be sent.
 *
 * Only server has access to this class
 */
public class WriteThreadServer implements Runnable {

    //writes in this thread
    private Thread thr;
    //object to send
    private Object object;
    //arraylist to send
    private ArrayList arrayList;
    //contains the socket of client server connection
    private NetworkUtil networkUtil;
    //checks whether object needs to be sent or arraylist.
    boolean isObj = false;
    boolean isArray = false;

    /**
     * starts thread which will be used to write in network stream
     *
     * @param networkUtil contains the socket of client server connection
     * @param object data to send in object form
     */
    public WriteThreadServer(NetworkUtil networkUtil,Object object) {
        this.object=object;
        isArray = false;
        isObj = true;
        this.networkUtil=networkUtil;
        this.thr = new Thread(this);
        thr.start();
    }

    /**
     * starts thread which will be used to write in network stream
     *
     * @param networkUtil contains the socket of client server connection
     * @param arrayList data to send in arraylist form
     */
    public WriteThreadServer(NetworkUtil networkUtil,ArrayList arrayList) {
        this.arrayList = arrayList;
        isArray =true;
        isObj=false;
        this.networkUtil=networkUtil;
        this.thr = new Thread(this);
        thr.start();
    }

    /**
     * after new thread starts running, it writes a object or arraylist and exits.
     */
    public void run()
    {
        try {
            if (networkUtil != null) {
                System.out.println("a new server write thread started ");
                if(isObj)
                    networkUtil.write(object);
                else if(isArray)
                    networkUtil.write(arrayList);
            }
        }
        catch(Exception e) {
            System.out.println (e);
        }
    }
}



