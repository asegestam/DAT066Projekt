package dat066.dat066_projekt;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class StatsFragment extends Fragment  {

    private View view;
    ArrayList array = new ArrayList();
    double data = 0;

    public StatsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stats, container, false);
        ArrayList elevation = this.getArguments().getParcelableArrayList("array");
        Log.d(TAG,"array in stats: "+elevation.size());
        setPlotData(elevation);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    private void setPlotData(ArrayList array){

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        DataPoint[] dataPoints = new DataPoint[array.size()];
        for (int i = 0; i < array.size(); i++) {
            dataPoints[i] = new DataPoint(i, (double) array.get(i));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        series.setColor(Color.BLACK);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setMaxX(60);
        /*graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(15);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().set;
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);*/
        graph.removeAllSeries();
        graph.addSeries(series);
    }
}
