package eu.andlabs.studiolounge;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatListAdapter extends BaseAdapter implements ChatListner{

	
	private List<ChatMessage> chatMessages;
	private Context context;

	public ChatListAdapter(Context context){
		this.context=context;
	}

	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return chatMessages.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        View v= convertView;
			if (convertView == null)
	            v = inflater.inflate(R.layout.chat_list_entry, null);
			
			((TextView)v.findViewById(R.id.sender)).setText(chatMessages.get(position).getSender());
			((TextView)v.findViewById(R.id.msg_text)).setText(chatMessages.get(position).getMessage());
		return v;
	}



	@Override
	public void onChatMessageRecieved(ChatMessage message) {
		chatMessages.add(message);
		notifyDataSetChanged();
		
	}



}
