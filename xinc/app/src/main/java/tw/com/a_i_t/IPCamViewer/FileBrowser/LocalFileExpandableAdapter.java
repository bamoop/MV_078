package tw.com.a_i_t.IPCamViewer.FileBrowser;

import android.content.Context;
import android.util.Log;
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

import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.FileNode;
import tw.com.a_i_t.IPCamViewer.FileBrowser.ViewTag;
import tw.com.a_i_t.IPCamViewer.R;

/**
 * Created by bzmoop on 2015/11/12 0012.
 */
public class LocalFileExpandableAdapter  extends BaseExpandableListAdapter{
    private Context context;
    private LayoutInflater mInflater;
    private Map<String, List<FileNode>> map;
    private ArrayList<String> parent;
    private int childrencoun=0;

    public LocalFileExpandableAdapter(Context paramContext, LayoutInflater paramLayoutInflater, Map<String, List<FileNode>> paramMap, ArrayList<String> paramArrayList)
    {
        this.context = paramContext;
        this.mInflater = paramLayoutInflater;
        this.parent = paramArrayList;
        this.map = paramMap;
    }
    @Override
    public int getGroupCount() {
        Log.i("moopadap", "父栏目大小" + parent.size());
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
        Log.i("moopadap", "子栏目数量" +size);
        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        Log.i("moopadap", "getGroup" + (String)this.parent.get(groupPosition));
        return this.parent.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String str = parent.get(groupPosition);
        Log.i("moopadap", "getChild" + ((List) this.map.get(str)).get(childPosition));
        return (map.get(str)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        Log.i("moopadap", "getGroupId" + groupPosition);
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Log.i("moopadap", "getChildId" + childPosition);
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.i("moopadap", "getGroupView" );
        Holder holder = null;
        String key=this.parent.get(groupPosition);
        String childposition="0";
        if (map.size()>0){
        Log.i("moopadap","map.get(key).size()="+ String.valueOf(map.get(key).size()));
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
        Log.i("moopadap", "getChildView" );
        ViewTag viewTag;
        String key=this.parent.get(groupPosition);
        if (convertView==null){
            convertView=mInflater.inflate(R.layout.local_filelist_row,null);
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
