//package dat066.dat066_projekt;
//
//import android.database.Cursor;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ListFragment;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.Loader;
//import android.widget.SearchView;
//import android.support.v4.widget.SimpleCursorAdapter;
//import android.support.v4.widget.CursorAdapter;
//
//public class CursorLoaderListFragment extends ListFragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
//
//    SimpleCursorAdapter mAdapter;
//
//    String mCurFilter;
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState){
//        super.onActivityCreated(savedInstanceState);
//
//        setEmptyText("No activites saved");
//        setHasOptionsMenu(true);
//
//        mAdapter = new SimpleCursorAdapter(getActivity(), R.id.textView2, null,
//    }
//    @NonNull
//    @Override
//    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
//
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//
//    }
//
//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        return false;
//    }
//}
