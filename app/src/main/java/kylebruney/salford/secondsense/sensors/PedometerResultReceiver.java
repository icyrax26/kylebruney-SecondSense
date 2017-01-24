package kylebruney.salford.secondsense.sensors;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class PedometerResultReceiver extends ResultReceiver {

	public static final int RESULTCODE_ERROR = -1;
	public static final String EXTRA_ERRORMSG = "PedometerResultReceiver.ERRORMSG";
	
	public static final int RESULTCODE_UPDATE = 1;
	public static final String EXTRA_X = "PedometerResultReceiver.X";

	
    private Receiver mReceiver;

    public PedometerResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
    	public void newEvent(float x);
        public void error(String error);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
        	if (resultCode == RESULTCODE_ERROR) {
        		mReceiver.error(resultData.getString(EXTRA_ERRORMSG));        		
        	} else {
        		float x = resultData.getFloat(EXTRA_X);
        		mReceiver.newEvent(x);
        	}
        }
    }

}