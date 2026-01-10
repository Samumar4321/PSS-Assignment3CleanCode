package it.marcosoft.ticketwave.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.marcosoft.ticketwave.EventModel.Event;
import it.marcosoft.ticketwave.R;
import it.marcosoft.ticketwave.data.LikedData;
import it.marcosoft.ticketwave.util.db.DBHelperLiked;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.eventList = eventList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.event_list_item_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Event event = eventList.get(position);

        EventInfo eventInfo = mapEventToEventInfo(event);

        bindEventInfoToViewHolder(viewHolder, eventInfo);
        
        setupDoubleTapLikeGesture(viewHolder, eventInfo);
    }

    private EventInfo mapEventToEventInfo(Event event){
        String description = "";
        String imgUrl = "";
        
        if(!event.getClassifications().isEmpty())
            description = event.getClassifications().get(0).toStringPretty();
        
        if(!event.getImages().isEmpty())
            imgUrl = event.getImages().get(0).getUrlImage();

        return new EventInfo(
            event.getName(),
            event.getVenue().getName(),
            event.getDate(),
            description,
            imgUrl,
            event.getId()
        );
    }

    private void bindEventInfoToViewHolder(ViewHolder viewHolder, EventInfo eventInfo){
        viewHolder.textTitle.setText(eventInfo.title());
        viewHolder.textLocation.setText(eventInfo.venueName());
        viewHolder.textDate.setText(eventInfo.eventDate());
        viewHolder.textDescription.setText(eventInfo.description());
        viewHolder.tagCard.setTag(eventInfo.eventId());
        Picasso.get().load(eventInfo.imgUrl()).into(viewHolder.imageView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDoubleTapLikeGesture(ViewHolder viewHolder, EventInfo eventInfo){
        GestureDetector gestureDetector = new GestureDetector(viewHolder.itemView.getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                        return super.onSingleTapConfirmed(e);
                    }

                    @Override
                    public boolean onDoubleTap(@NonNull MotionEvent e) {
                        toggleLikeStatus(viewHolder, eventInfo);
                        return true;
                    }
                });
                
        viewHolder.itemView.setOnTouchListener((v, eventCard) -> gestureDetector.onTouchEvent(eventCard));
    }

    private void toggleLikeStatus(ViewHolder viewHolder, EventInfo eventInfo){
        String userId = "userId";

        if (isEventLiked(viewHolder.itemView.getContext(), eventInfo.eventId(), userId)) {
            removeEventFromLiked(viewHolder, eventInfo, userId);
        } else {
            addEventToLiked(viewHolder, eventInfo, userId);
        }
    }

    private boolean isEventLiked(Context context, String eventId, String userId) {
        DBHelperLiked dbHelper = new DBHelperLiked(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DBHelperLiked.COLUMN_EVENT_ID + " = ? AND " +
                DBHelperLiked.COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {eventId, userId};

        Cursor cursor = db.query(
                DBHelperLiked.TABLE_LIKED_EVENTS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean eventLiked = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return eventLiked;
    }

    private void removeEventFromLiked(ViewHolder viewHolder, EventInfo eventInfo, String userId) {
        ImageView dislikeAnimationView = viewHolder.itemView.findViewById(R.id.dislike_animation);
        playAnimation(dislikeAnimationView);

        DBHelperLiked dbHelper = new DBHelperLiked(viewHolder.itemView.getContext());
        dbHelper.removeLikedEvent(eventInfo.eventId());

        Toast.makeText(viewHolder.itemView.getContext(),
                "Disliked event!", Toast.LENGTH_SHORT).show();
    }

    private void addEventToLiked(ViewHolder viewHolder, EventInfo eventInfo, String userId) {
        ImageView likeAnimationView = viewHolder.itemView.findViewById(R.id.like_animation);
        playAnimation(likeAnimationView);

        DBHelperLiked dbHelper = new DBHelperLiked(viewHolder.itemView.getContext());
        dbHelper.addLikedEvent(new LikedData(
                eventInfo.eventId(),
                userId,
                eventInfo.title(),
                eventInfo.venueName(),
                eventInfo.eventDate(),
                eventInfo.description(),
                eventInfo.imgUrl()
        ));

        Toast.makeText(viewHolder.itemView.getContext(),
                "Liked event!", Toast.LENGTH_SHORT).show();
    }

    private void playAnimation(ImageView animationView) {
        Drawable drawable = animationView.getDrawable();

        animationView.setAlpha(1f);

        if (drawable instanceof AnimatedVectorDrawableCompat animatedDrawableCompat) {
            animatedDrawableCompat.start();
        } else if (drawable instanceof AnimatedVectorDrawable animatedDrawable) {
            animatedDrawable.start();
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView textTitle;
        final TextView textLocation;
        final TextView textDate;
        final TextView textDescription;
        final ImageView imageView;
        final LinearLayout tagCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.event_name);
            textLocation = itemView.findViewById(R.id.event_location);
            textDate = itemView.findViewById(R.id.event_date);
            imageView = itemView.findViewById(R.id.event_image);
            textDescription = itemView.findViewById(R.id.event_description);
            tagCard = itemView.findViewById(R.id.cardId);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setEventList(List<Event> events) {
        this.eventList = events;
        notifyDataSetChanged();
    }

    public record EventInfo(
        String title, 
        String venueName, 
        String eventDate, 
        String description, 
        String imgUrl, 
        String eventId) {}
}
