package com.simonk.project.ppoproject.ui.pictures;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.GalleryFragmentBinding;
import com.simonk.project.ppoproject.error.ErrorLayout;
import com.simonk.project.ppoproject.model.Picture;
import com.simonk.project.ppoproject.permission.PermissionHandler;
import com.simonk.project.ppoproject.permission.PermissionRequest;
import com.simonk.project.ppoproject.permission.PermissionsAccessor;
import com.simonk.project.ppoproject.utils.ImageUtil;
import com.simonk.project.ppoproject.utils.GalleryCursorLoader;
import com.simonk.project.ppoproject.viewmodels.GalleryViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryFragment extends Fragment {

    private GalleryAdapter mAdapter;

    private static final int GALLERY_IMAGE_LOADER = 1;

    private PermissionHandler mPermissionHandler;
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;

    private static final int REQUEST_PHOTO = 0;

    private String mPendingImageInCachePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        GalleryFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.gallery_fragment, parent, false);

        binding.appBarInclude.toolbarLayout.toolbar.setTitle("Gallery");
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.appBarInclude.toolbarLayout.toolbar);

        mFab = binding.galleryPhotoFab;
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionRequest permission = new PermissionRequest(Manifest.permission.CAMERA);
                permission.setAction(new PermissionRequest.PermissionAction() {
                    @Override
                    public void onPermissionGranted(PermissionRequest permission) {
                        boolean isHaveAccess = PermissionsAccessor.tryToAccessCameraPermission(GalleryFragment.this);
                        if (isHaveAccess) {
                            File cacheImageFile = new File(getContext().getExternalCacheDir() + "/photo", ImageUtil.getNewImageFileName());
                            Uri uri = Uri.fromFile(cacheImageFile);
                            Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(captureImage, REQUEST_PHOTO);

                            mPendingImageInCachePath = cacheImageFile.getPath();
                        }
                    }
                });
                mPermissionHandler.postImmediately(permission, 1);
            }
        });

        mRecyclerView = binding.galleryRecyclerView;
        mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false));
        mAdapter = new GalleryAdapter(requireContext());
        mAdapter.setOnItemClickListener(new GalleryAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(View v, int position) {
                finish(mAdapter.getPictures().get(position).getPath());
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        ErrorLayout permissionRequestLayout = binding.galleryPermissionLayout;
        permissionRequestLayout.shouldAnimate(false);
        mPermissionHandler = PermissionHandler.with(this).connect(permissionRequestLayout);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUiIfHavePermissions();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PHOTO) {
                finish(mPendingImageInCachePath);
            }
        }
    }

    private void finish(String path) {
        GalleryViewModel model = ViewModelProviders.of(getActivity()).get(GalleryViewModel.class);
        model.setPath(path);
        final NavController navController =
                Navigation.findNavController(getView());
        navController.navigateUp();
    }

    private void updateUiIfHavePermissions() {
        PermissionRequest permission = new PermissionRequest(Manifest.permission.READ_EXTERNAL_STORAGE);
        permission.addDescriptions(getResources(), R.array.gallery_storage_permission);
        permission.setAction(new PermissionRequest.PermissionAction() {
            @Override
            public void onPermissionGranted(PermissionRequest permission) {
                startLoading();
            }
        });
        mPermissionHandler.postDelayed(permission, 0);

        mRecyclerView.setVisibility(View.GONE);
        mFab.setVisibility(View.GONE);
    }

    private void startLoading() {
        getLoaderManager().initLoader(GALLERY_IMAGE_LOADER, null, new GalleryLoaderCallback()).forceLoad();
    }

    private void updateUi(List<Picture> pictures) {
        mAdapter.setPictures(pictures);
        mRecyclerView.setVisibility(View.VISIBLE);
        mFab.setVisibility(View.VISIBLE);
    }

    private static class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapterViewHolder> {

        private List<Picture> pictureList;

        private Context mContext;

        private ItemClickListener mItemClickListener;

        public GalleryAdapter(Context context) {
            mContext = context;
        }

        public void setPictures(List<Picture> pictures) {
            pictureList = pictures;
            if (pictureList == null) {
                pictureList = new ArrayList<>();
            }
            notifyDataSetChanged();
        }

        public List<Picture> getPictures() {
            return pictureList;
        }

        public interface ItemClickListener {
            void onItemClicked(View v, int position);
        }

        public void setOnItemClickListener(ItemClickListener onItemClickListener) {
            mItemClickListener = onItemClickListener;
        }

        public void removeOnItemClickListener() {
            mItemClickListener = null;
        }

        @NonNull
        @Override
        public GalleryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            final ImageView view = (ImageView) inflater.inflate(R.layout.gallery_item, parent, false);

            GalleryAdapterViewHolder imageViewHolder = new GalleryAdapterViewHolder(view);
            imageViewHolder.setOnClickListener(mItemClickListener);
            return imageViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryAdapterViewHolder holder, int position) {
            holder.bind(pictureList.get(position));
        }

        @Override
        public int getItemCount() {
            return pictureList == null ? 0 : pictureList.size();
        }
    }

    private static class GalleryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private GalleryAdapter.ItemClickListener mClickListener;

        public GalleryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void setOnClickListener(GalleryAdapter.ItemClickListener onClickListener) {
            this.mClickListener = onClickListener;
        }

        public void bind(Picture picture) {
            Glide.with(itemView.getContext())
                    .load(new File(picture.getPath()))
                    .into((ImageView) itemView);

        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClicked(v, getAdapterPosition());
            }
        }
    }

    private class GalleryLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final int INDEX_ID = 0;
        private static final int INDEX_DATA_PATH = 1;
        private static final int INDEX_WIDTH = 2;
        private static final int INDEX_HEIGHT = 3;
        private static final int INDEX_MIME_TYPE = 4;
        private static final int INDEX_DATE_MODIFIED = 5;

        @Override
        @NonNull
        public Loader onCreateLoader(int id, Bundle args) {
            return new GalleryCursorLoader(getContext());
        }

        @Override
        public void onLoadFinished(@NonNull Loader loader, Cursor data) {
            if (data != null && data.moveToFirst()) {

                ArrayList<Picture> items = new ArrayList<>();
                for (int i = 0; i < 1; i++) {
                    data.moveToFirst();

                    do {
                        String id = data.getString(INDEX_ID);
                        int width = data.getInt(INDEX_WIDTH);
                        int height = data.getInt(INDEX_HEIGHT);
                        String mimeType = data.getString(INDEX_MIME_TYPE);
                        String dateModified = data.getString(INDEX_DATE_MODIFIED);
                        String dataPath = data.getString(INDEX_DATA_PATH);

                        Picture item = new Picture();
                        item.setPath(dataPath);

                        boolean valid = checkIsImageFileValid(new File(dataPath));
                        if (valid) {
                            items.add(item);
                        }
                    } while (data.moveToNext());
                }

                updateUi(items);
            } else {
                updateUi(null);
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {
            mAdapter.setPictures(null);
        }

        private boolean checkIsImageFileValid(File file) {
            // TODO: проверить что это действительно картинка
            return file.isFile() && file.exists() && file.canRead() && !file.isHidden();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        mPermissionHandler.onSomePermissionResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
