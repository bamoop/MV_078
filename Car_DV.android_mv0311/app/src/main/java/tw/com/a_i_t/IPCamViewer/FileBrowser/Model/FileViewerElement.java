package tw.com.a_i_t.IPCamViewer.FileBrowser.Model;

import org.w3c.dom.Node ;

public interface FileViewerElement {

	String getElementName() ;

	boolean matchElement(Node node) ;

	public enum DirectoryElement implements FileViewerElement {
		file, amount ;

		@Override
		public String getElementName() {
			return name() ;
		}

		@Override
		public boolean matchElement(Node node) {
			return node.getNodeName().equalsIgnoreCase(name()) ;
		}
	}

	public enum FileElement implements FileViewerElement {

		name, format, size, attr, time ;

		@Override
		public String getElementName() {
			return name() ;
		}

		@Override
		public boolean matchElement(Node node) {
			return node.getNodeName().equalsIgnoreCase(name()) ;
		}
	}
}