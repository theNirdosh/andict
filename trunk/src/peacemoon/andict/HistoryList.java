package peacemoon.andict;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryList implements Parcelable {
	private ArrayList<String> lstHistory;
	
	public ArrayList<String> getList()
	{
		return lstHistory;
	}
	
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeStringList(lstHistory);
	}
	
	public static final Parcelable.Creator<HistoryList> CREATOR = new Parcelable.Creator<HistoryList>() {
		public HistoryList createFromParcel(Parcel in)
		{
			return new HistoryList(in);
		}
		public HistoryList[] newArray(int size)
		{
			return new HistoryList[size];
		}
	};
	
	public HistoryList(ArrayList<String> in)
	{
		lstHistory = in;
	}
	
	private HistoryList(Parcel in)
	{
		in.readStringList(lstHistory);
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
