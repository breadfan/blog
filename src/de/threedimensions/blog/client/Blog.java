package de.threedimensions.blog.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.threedimensions.blog.client.model.BlogEntryJs;
import de.threedimensions.blog.client.model.BlogEntryRefJs;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Blog implements EntryPoint, AsyncRestCallbackHandler {

    private VerticalPanel contentMiddlePanel;
    private BlogRestClient blogRestClient = new BlogRestClient();

    /**
     * Entry point method.
     */
    public void onModuleLoad() {
	HorizontalPanel mainPanel = new HorizontalPanel();

	contentMiddlePanel = new VerticalPanel();
	mainPanel.add(contentMiddlePanel);

	HorizontalPanel commentPanel = createCommentPanel();
	contentMiddlePanel.add(commentPanel);

	VerticalPanel navPanel = createNavPanel();
	mainPanel.add(navPanel);

	RootPanel.get("blogPanel").add(mainPanel);

	blogRestClient.getPosts(this);
    }

    private VerticalPanel createNavPanel() {
	VerticalPanel navPanel = new VerticalPanel();
	navPanel.add(new Hyperlink("Post 1", "post1"));
	navPanel.add(new Hyperlink("Post 2", "post2"));
	return navPanel;
    }

    private HorizontalPanel createCommentPanel() {
	HorizontalPanel commentPanel = new HorizontalPanel();
	final TextArea commentInputTextArea = new TextArea();
	commentInputTextArea.setVisibleLines(1);
	commentInputTextArea.addFocusHandler(new FocusHandler() {
	    @Override
	    public void onFocus(FocusEvent event) {
		commentInputTextArea.setVisibleLines(5);
	    }
	});
	commentInputTextArea.addBlurHandler(new BlurHandler() {

	    @Override
	    public void onBlur(BlurEvent event) {
		commentInputTextArea.setVisibleLines(1);
	    }
	});
	commentPanel.add(commentInputTextArea);

	Button commentButton = new Button("Post a comment", new ClickHandler() {
	    public void onClick(ClickEvent event) {
		commentInputTextArea.setVisible(true);
	    }
	});
	commentPanel.add(commentButton);
	return commentPanel;
    }

    @Override
    public void blogEntryReceived(BlogEntryJs blogEntryJs) {
	Widget widget = new HTML("<h2>" + blogEntryJs.getHeading() + "</h2>" + blogEntryJs.getContent());
	contentMiddlePanel.add(widget);
    }

    @Override
    public void listOfBlogEntriesReceived(JsArray<BlogEntryRefJs> blogEntryRefJsArray) {
	for (int i = 0; i < blogEntryRefJsArray.length(); i++) {
	    BlogEntryRefJs blogEntryRefJs = blogEntryRefJsArray.get(i);
	    blogRestClient.getBlogEntry(blogEntryRefJs.getUrl(), this);
	}
    }

    @Override
    public void handleError(String errorMessage) {
	// TODO Auto-generated method stub

    }
}
