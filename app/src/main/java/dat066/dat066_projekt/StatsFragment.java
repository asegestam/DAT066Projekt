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
import android.view.animation.Animation;
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
    TextView headline;
    TextView elevationTextView;
    TextView speedTextView;
    GraphView elevationGraph;
    GraphView speedGraph;

    public StatsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stats, container, false);
        array = this.getArguments().getParcelableArrayList("elevationArray");
        headline = view.findViewById(R.id.stats_headling);
        elevationTextView = view.findViewById(R.id.stats_text);
        speedTextView = view.findViewById(R.id.speed_stats);
        elevationGraph = (GraphView) view.findViewById(R.id.elevation_graph);
        elevationGraph.setTitle("Elevation");
        speedGraph = (GraphView) view.findViewById(R.id.speed_graph);
        speedGraph.setTitle("Speed");
        setPlotData(array,elevationGraph);
        setPlotData(this.getArguments().getParcelableArrayList("speedArray"),speedGraph);
        setStatsText();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setStatsText(){
        headline.setText("Stats for your run on: \n");
        elevationTextView.setText(this.getArguments().getString("date")+"\n");
        elevationTextView.append("It lasted for: " + this.getArguments().get("time")+"\n\n");
        elevationTextView.append("Your highest elevation: " + Math.round(elevationGraph.getViewport().getMaxY(true)*100.0)/100.0 + " m\n");
        elevationTextView.append("Your lowest elevation: " + Math.round(elevationGraph.getViewport().getMinY(true)*100.0)/100.0 + " m");
        speedTextView.setText("Your highest speed: " + Math.round(speedGraph.getViewport().getMaxY(true)*100.0)/100.0 + " m/s\n");
        speedTextView.append("Your lowest speed: " + Math.round(speedGraph.getViewport().getMinY(true)*100.0)/100.0 + " m/s");





    }

    private void setPlotData(ArrayList array, GraphView graph){

        DataPoint[] dataPoints = new DataPoint[array.size()];
        for (int i = 0; i < array.size(); i++) {
            dataPoints[i] = new DataPoint(i*5, (double) array.get(i));
        }

        final LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        final double maxX = series.getHighestValueX();
        final GraphView graphView = graph;
        final double minX = series.getLowestValueX();
        final double maxY = series.getHighestValueY();
        final double minY = series.getLowestValueY();
        series.setColor(Color.BLACK);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);
        series.setDrawAsPath(true);
        series.setAnimated(true);
        graph.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                graphView.getViewport().setMinY(minY);
                graphView.getViewport().setMaxY(maxY);
                graphView.getViewport().setMinX(minX);
                graphView.getViewport().setMaxX(maxX);
                return false;

            }
        });
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(minY);
        graphView.getViewport().setMaxY(maxY);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(minX);
        graphView.getViewport().setMaxX(maxX);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);
        graphView.addSeries(series);
    }
}
