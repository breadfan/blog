package de.threedimensions.blog.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import de.threedimensions.blog.client.components.BlogEntryComponent;
import de.threedimensions.blog.client.components.Navbar;
import de.threedimensions.blog.client.components.PostEditComponent;
import de.threedimensions.blog.client.event.BlogEntryReceivedEvent;
import de.threedimensions.blog.client.event.EventHandler;
import de.threedimensions.blog.client.event.ListOfBlogEntryRefsReceivedEvent;
import de.threedimensions.blog.client.model.BlogEntryRefJs;
import de.threedimensions.blog.client.rest.BlogRestClient;
import de.threedimensions.blog.client.rest.ErrorHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Blog implements EntryPoint, ErrorHandler, EventHandler<ListOfBlogEntryRefsReceivedEvent>, EditorSupport {

    private BlogRestClient blogRestClient = new BlogRestClient(this);
    private Label feedbackLabel = new Label();
    private Tree blogArchiveTree = new Tree();
    private BlogEntryComponent blogEntryComponent;
    private PostEditComponent postEditPanel = new PostEditComponent(this);
    private PopupPanel popupPanel = new PopupPanel(false, true);
    private Label popupPanelLabel = new Label();

    /**
     * Entry point method.
     */
    public void onModuleLoad() {
	Navbar navbar = new Navbar(blogRestClient, this);
	RootPanel.get("navbar").add(navbar);

	RootPanel.get("ArchiveList").add(blogArchiveTree);

	blogEntryComponent = new BlogEntryComponent();
	blogEntryComponent.setVisible(false);
	getPanelForBlogEntry().add(blogEntryComponent);

	popupPanel.add(popupPanelLabel);
	popupPanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
	    public void setPosition(int offsetWidth, int offsetHeight) {
		int left = (Window.getClientWidth() - offsetWidth) / 3;
		int top = (Window.getClientHeight() - offsetHeight) / 3;
		popupPanel.setPopupPosition(left, top);
	    }
	});

	fetchPosts();
    }

    private RootPanel getPanelForBlogEntry() {
	return RootPanel.get("blogEntryComponent");
    }

    @Override
    public void handleError(String errorMessage) {
	feedbackLabel.setText(errorMessage);
    }

    @Override
    public void handleEvent(ListOfBlogEntryRefsReceivedEvent event) {
	JsArray<BlogEntryRefJs> blogEntryRefs = event.getContent();
	for (int i = 0; i < blogEntryRefs.length(); i++) {
	    BlogEntryRefJs blogEntryRef = blogEntryRefs.get(i);
	    blogArchiveTree.addItem(new TreeItem("Post id " + blogEntryRef.getId()));
	}

	BlogEntryRefJs blogEntryRefJs = event.getContent().get(0);
	if (blogEntryRefJs != null) {
	    final String blogPostUrl = blogEntryRefJs.getUrl();
	    fetchPost(blogPostUrl);
	}
    }

    private void fetchPost(final String blogPostUrl) {
	popupPanelLabel.setText("Fetching post ...");
	popupPanel.show();
	blogRestClient.getBlogEntry(blogPostUrl, new EventHandler<BlogEntryReceivedEvent>() {
	    @Override
	    public void handleEvent(BlogEntryReceivedEvent event) {
		blogEntryComponent.handleEvent(event);
		popupPanel.hide();
		blogEntryComponent.setVisible(true);
	    }
	});
    }

    /**
     * @see de.threedimensions.blog.client.EditorSupport#showEditor()
     */
    @Override
    public void showEditor() {
	getPanelForBlogEntry().remove(blogEntryComponent);
	getPanelForBlogEntry().add(postEditPanel);
    }

    /**
     * @see de.threedimensions.blog.client.EditorSupport#showBlog()
     */
    @Override
    public void showBlog(String url) {
	getPanelForBlogEntry().remove(postEditPanel);
	getPanelForBlogEntry().add(blogEntryComponent);
	fetchPost(url);
    }

    private void fetchPosts() {
	popupPanelLabel.setText("Loading list of posts ...");
	popupPanel.show();
	blogRestClient.getPosts(this);
    }
}
