package vesnell.pl.quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vesnell.pl.quiz.database.model.Quiz;
import vesnell.pl.quiz.utils.Resources;

/**
 * Created by alek6 on 19.04.2016.
 */
public class ListViewAdapter extends ArrayAdapter<Quiz> {

    private Context context;
    private List<Quiz> quizzes;

    static class ViewHolder {
        public TextView tvTitle;
        public TextView tvScore;
        public TextView tvState;
        public ImageView ivMainPhoto;
    }

    public ListViewAdapter(Context context, List<Quiz> quizzes) {
        super(context, R.layout.quiz_row_layout, quizzes);
        this.context = context;
        this.quizzes = quizzes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.quiz_row_layout, null);

            //configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) rowView.findViewById(R.id.title);
            viewHolder.tvScore = (TextView) rowView.findViewById(R.id.score);
            viewHolder.tvState = (TextView) rowView.findViewById(R.id.state);
            viewHolder.ivMainPhoto = (ImageView) rowView.findViewById(R.id.image);
            rowView.setTag(viewHolder);
        }

        //fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        Quiz quiz = quizzes.get(position);
        String title = quiz.getTitle();
        holder.tvTitle.setText(title);

        if (quiz.hasState()) {
            holder.tvState.setVisibility(View.VISIBLE);
            String state = Resources.getString(R.string.quiz_last_state, quiz.getState());
            holder.tvState.setText(state);
        } else {
            holder.tvState.setVisibility(View.GONE);
        }
        if (quiz.hasScore()) {
            holder.tvScore.setVisibility(View.VISIBLE);
            String score = Resources.getString(R.string.quiz_last_score, quiz.getCorrectAnswers(),
                    quiz.getQuestionsCount(), quiz.getScore());
            holder.tvScore.setText(score);
        } else {
            holder.tvScore.setVisibility(View.GONE);
        }
        Picasso.with(context).load(quizzes.get(position).getMainPhoto())
                .resizeDimen(R.dimen.list_item_height, R.dimen.list_item_width).centerCrop().into(holder.ivMainPhoto);

        return rowView;
    }
}
