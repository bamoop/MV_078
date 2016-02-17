package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model.FileNode;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.R;

/**
 * Created by bzmoop on 2015/11/12 0012.
 */
public class LocalFileVideoExpandableAdapter extends BaseExpandableListAdapter{
    private Context context;
    private LayoutInflater mInflater;
    private Map<String, List<FileNode>> map;
    private ArrayList<String> parent;
    private int childrencoun=0;

    public LocalFileVideoExpandableAdapter(Context paramContext, LayoutInflater paramLayoutInflater, Map<String, List<FileNode>> paramMap, ArrayList<String> paramArrayList)
    {
        this.context = paramContext;
        this.mInflater = paramLayoutInflater;
        this.parent = paramArrayList;
        this.map = paramMap;
    }
    @Override
    public int getGroupCount() {
        return  parent.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String str =this.parent.get(groupPosition);
        int size=0;
        if (map.size()>0){
            size=this.map.get(str).size();
            this.childrencoun=size;
        }
        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.parent.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String str = parent.get(groupPosition);
        return (map.get(str)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Holder holder = null;
        String key=this.parent.get(groupPosition);
        String childposition="0";
        if (map.size()>0){
            childposition=  String.valueOf(map.get(key).size());
        }
        if (convertView==null){
            convertView=mInflater.inflate(R.layout.local_expand_parent,null);
            holder=new Holder();
            convertView.setTag(holder);
            holder.tv_name= (TextView) convertView.findViewById(R.id.tv_local_expandName);
            holder.tv_Position= (TextView) convertView.findViewById(R.id.tv_local_expandPosition);
        }else {
            holder= (Holder) convertView.getTag();
        }
        holder.tv_name.setText(key);
        holder.tv_Position.setText(childposition);
        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewTag viewTag;
        String key=this.parent.get(groupPosition);
        if (convertView==null){
            convertView=mInflater.inflate(R.layout.local_filelist_video_row,null);
            viewTag=new ViewTag(
                    (ImageView) convertView
                            .findViewById(R.id.fileListThumbnail),
                    (TextView) convertView.findViewById(R.id.fileListName),
                    (TextView) convertView.findViewById(R.id.fileListTime),
                    (TextView) convertView.findViewById(R.id.fileListSize),
                    map.get(key).get(childPosition),
                    (CheckedTextView)convertView.findViewById(R.id.fileListCheckBox));
            convertView.setTag(viewTag);
        }else {
            viewTag= (ViewTag) convertView.getTag();
        }
        viewTag.mFileNode =  map.get(key).get(childPosition);
        String filename = viewTag.mFileNode.mName
                .substring(viewTag.mFileNode.mName.lastIndexOf("/") + 1);
        viewTag.mFilename.setText(filename);
        viewTag.mTime.setText(viewTag.mFileNode.mTime);
        viewTag.mCheckBox.setChecked(viewTag.mFileNode.mSelected);
        viewTag.setSize(viewTag.mFileNode.mSize);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    class Holder
    {
        TextView tv_name;
        TextView tv_Position;

    }
}
