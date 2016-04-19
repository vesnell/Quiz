package vesnell.pl.quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vesnell.pl.quiz.model.Quiz;

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
            rowView.setTag(viewHolder);
        }

        //fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        Quiz quiz = quizzes.get(position);
        String title = quiz.getTitle();
        holder.tvTitle.setText(title);

        return rowView;
    }
}
