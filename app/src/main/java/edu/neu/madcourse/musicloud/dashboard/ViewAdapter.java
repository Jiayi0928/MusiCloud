package edu.neu.madcourse.musicloud.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import edu.neu.madcourse.musicloud.dashboard.BlankFragment;
import edu.neu.madcourse.musicloud.dashboard.PostsFragment;

public class ViewAdapter extends FragmentStateAdapter {
    private FragmentTransaction fragmentTransaction;
    public ViewAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }



    @Override
    public int getItemCount() {
        return 2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new PostsFragment();
            case 1:
                return new LikesFragment();
            default:
                return new BlankFragment();
        }

    }


}
