package vesnell.pl.quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vesnell.pl.quiz.database.model.Quiz;

/**
 * Created by alek6 on 19.04.2016.
 */
public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private List<Quiz> quizzes = new ArrayList<Quiz>();

    static class ViewHolder {
        public RelativeLayout itemMainLayout;
        public LinearLayout secondaryLayout;
        public TextView tvTitle;
        public TextView tvScore;
        public TextView tvState;
        public ImageView ivMainPhoto;
    }

    public ListViewAdapter(Context context) {
        this.context = context;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return quizzes.size();
    }

    @Override
    public Object getItem(int position) {
        return quizzes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.quiz_row_layout, null);

            //configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.itemMainLayout = (RelativeLayout) rowView.findViewById(R.id.itemMainLayout);
            viewHolder.secondaryLayout = (LinearLayout) rowView.findViewById(R.id.secondaryLayout);
            viewHolder.tvTitle = (TextView) rowView.findViewById(R.id.title);
            viewHolder.tvScore = (TextView) rowView.findViewById(R.id.score);
            viewHolder.tvState = (TextView) rowView.findViewById(R.id.state);
            viewHolder.ivMainPhoto = (ImageView) rowView.findViewById(R.id.image);
            rowView.setTag(viewHolder);
        }

        //fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT , (int) context.getResources().getDimension(R.dimen.list_item_height));
        holder.itemMainLayout.setLayoutParams(layoutParams);

        Quiz quiz = quizzes.get(position);
        String title = quiz.getTitle();
        holder.tvTitle.setText(title);

        if (quiz.hasState() || quiz.hasScore()) {
            holder.secondaryLayout.setVisibility(View.VISIBLE);
        } else {
            holder.secondaryLayout.setVisibility(View.GONE);
        }
        if (quiz.hasState()) {
            holder.tvState.setVisibility(View.VISIBLE);
            String state = context.getResources().getString(R.string.quiz_last_state, quiz.getState());
            holder.tvState.setText(state);
        } else {
            holder.tvState.setVisibility(View.GONE);
        }
        if (quiz.hasScore()) {
            holder.tvScore.setVisibility(View.VISIBLE);
            String score = context.getResources().getString(R.string.quiz_last_score, quiz.getCorrectAnswers(),
                    quiz.getQuestionsCount(), quiz.getScore());
            holder.tvScore.setText(score);
        } else {
            holder.tvScore.setVisibility(View.GONE);
        }
        Picasso.with(context).load(quizzes.get(position).getMainPhoto())
                .resizeDimen(R.dimen.list_item_width, R.dimen.list_item_height).centerCrop().into(holder.ivMainPhoto);

        return rowView;
    }
}
