package dat066.dat066_projekt;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class StatsFragment extends Fragment {

    private View view;
    double data = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stats, container, false);

        Bundle bundle = this.getArguments();

        if(bundle != null)
            setPlotData(bundle.getParcelableArrayList("elevation"));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Stats-Fragment");
    }

    public void setPlotData(ArrayList array){

        GraphView graph = view.findViewById(R.id.graph);
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
        graph.addSeries(series);
        Log.e(TAG, "Added point: " + data);
    }


}
