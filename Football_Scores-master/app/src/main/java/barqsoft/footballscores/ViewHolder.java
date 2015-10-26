package barqsoft.footballscores;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ViewHolder {
    public TextView mHomeName;
    public TextView mAwayName;
    public TextView mScore;
    public TextView mDate;
    public ImageView mHomeCrest;
    public ImageView mAwayCrest;
    public double mMatchId;

    public ViewHolder(View view) {
        mHomeName = (TextView) view.findViewById(R.id.home_name);
        mAwayName = (TextView) view.findViewById(R.id.away_name);
        mScore = (TextView) view.findViewById(R.id.score_textview);
        mDate = (TextView) view.findViewById(R.id.data_textview);
        mHomeCrest = (ImageView) view.findViewById(R.id.home_crest);
        mAwayCrest = (ImageView) view.findViewById(R.id.away_crest);
    }
}
