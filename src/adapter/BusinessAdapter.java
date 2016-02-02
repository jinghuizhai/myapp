package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import com.example.myapp.R;

import java.util.ArrayList;

/**
 * Created by UI on 2015/12/31.
 */
public class BusinessAdapter extends SimpleAdapter{
    private LayoutInflater layout;
    public BusinessAdapter(Context context, ArrayList data,
                     int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View result = super.getView(position,convertView, parent);
//        layout = getLayoutInflater();
//        if (result == null) {
//            //载入指定布局
//            layout.inflate(R.layout.business_listview, null);
//        }
        return result;
    }
}
