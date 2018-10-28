package com.techbyself.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.anriod.vidioplayer.R;
import com.techbyself.vodplay.VideoPlayerActivity;
import com.techbyself.vodplay.util.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class VodioListFragment extends Fragment {
    private RecyclerView mVidioRecyclerView;
    private VidioAdapter mAdapter;
    private static final String ARG_POSITION = "type";

    public static VodioListFragment newInstance(int position) {
        VodioListFragment f = new VodioListFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vidio_list, container, false);

        mVidioRecyclerView = (RecyclerView) view
                .findViewById(R.id.vidio_recycler_view);
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mVidioRecyclerView.setLayoutManager(layoutManager);
        mVidioRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        int  pagetype=(int)getArguments().getSerializable(ARG_POSITION);
        updateUI(pagetype);

        return view;
    }

    private void updateUI(int pagetype) {
        VidioLab vidioLab = VidioLab.get();
        List<Video> vidios =new ArrayList<Video>();
        if(pagetype==0) {
            vidios = vidioLab.getAllVidios();
        }
        else{
            vidios = vidioLab.getRecentVidios();
        }

        mAdapter = new VidioAdapter(vidios);
        mVidioRecyclerView.setAdapter(mAdapter);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Video mCrime;

        private TextView mTitleTextView;
        private  TextView sNameTextView;
        private ImageView mSolvedImageView;


        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_vidio, parent, false));
            itemView.setOnClickListener(this);
            sNameTextView = (TextView) itemView.findViewById(R.id.vidio_name);
            mTitleTextView=(TextView) itemView.findViewById(R.id.vidio_title);
        }

        public void bind(Video crime) {
            mCrime = crime;
            sNameTextView.setText(mCrime.getVidioName());
            mTitleTextView.setText(mCrime.getTitle());
        }

        @Override
        public void onClick(View view) {
          String videoid=mCrime.getVidioid();
            MyDatabaseHelper.addRecentStudyRecord(mCrime);
        VideoPlayerActivity.startPlayerActivity(VodioListFragment.this.getActivity(),videoid,mCrime.getNceth());

        }
    }

    private class VidioAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Video> mVidios;

        public VidioAdapter(List<Video> vidios) {
            mVidios = vidios;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Video vidio = mVidios.get(position);
            holder.bind(vidio);
        }

        @Override
        public int getItemCount() {
            return mVidios.size();
        }
    }

}
