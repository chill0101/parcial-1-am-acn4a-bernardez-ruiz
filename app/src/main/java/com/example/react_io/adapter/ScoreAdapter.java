package com.example.react_io.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.react_io.R;
import com.example.react_io.models.GameScore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<GameScore> scores = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public void updateScores(List<GameScore> newScores) {
        this.scores = newScores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        GameScore score = scores.get(position);
        holder.bind(score);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGameType, tvTime, tvErrors, tvDate;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGameType = itemView.findViewById(R.id.tvGameType);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvErrors = itemView.findViewById(R.id.tvErrors);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(GameScore score) {
            tvGameType.setText(score.getGameType());
            tvTime.setText(formatTime(score.getTimeInMillis()));
            tvErrors.setText("Errores: " + score.getErrors());
            tvDate.setText(dateFormat.format(score.getTimestamp().toDate()));
        }

        private String formatTime(long timeInMillis) {
            long seconds = timeInMillis / 1000;
            long milliseconds = timeInMillis % 1000;
            return String.format(Locale.getDefault(), "%d.%03ds", seconds, milliseconds);
        }
    }
}