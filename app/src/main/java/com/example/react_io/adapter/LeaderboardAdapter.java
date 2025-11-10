package com.example.react_io.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.react_io.R;
import com.example.react_io.models.GameScore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<GameScore> scores = new ArrayList<>();

    public void updateScores(List<GameScore> newScores) {
        this.scores = newScores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        GameScore score = scores.get(position);
        holder.bind(score, position + 1);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPosition, tvUsername, tvTime, tvErrors, tvGameType;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvErrors = itemView.findViewById(R.id.tvErrors);
            tvGameType = itemView.findViewById(R.id.tvGameType);
        }

        public void bind(GameScore score, int position) {
            tvPosition.setText(String.valueOf(position));
            tvUsername.setText(score.getUsername());
            tvTime.setText(formatTime(score.getTimeInMillis()));
            tvErrors.setText(String.valueOf(score.getErrors()));
            tvGameType.setText(score.getGameType());

            // Destacar los primeros 3 lugares
            if (position <= 3) {
                int color;
                switch (position) {
                    case 1:
                        color = itemView.getContext().getColor(android.R.color.holo_orange_light); // Oro
                        break;
                    case 2:
                        color = itemView.getContext().getColor(android.R.color.darker_gray); // Plata
                        break;
                    case 3:
                        color = itemView.getContext().getColor(android.R.color.holo_orange_dark); // Bronce
                        break;
                    default:
                        color = itemView.getContext().getColor(android.R.color.transparent);
                        break;
                }
                itemView.setBackgroundColor(color);
            } else {
                itemView.setBackgroundColor(itemView.getContext().getColor(android.R.color.transparent));
            }
        }

        private String formatTime(long timeInMillis) {
            long seconds = timeInMillis / 1000;
            long milliseconds = timeInMillis % 1000;
            return String.format(Locale.getDefault(), "%d.%03ds", seconds, milliseconds);
        }
    }
}