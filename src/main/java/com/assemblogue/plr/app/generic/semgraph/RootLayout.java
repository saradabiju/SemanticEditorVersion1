package com.assemblogue.plr.app.generic.semgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.assemblogue.plr.lib.EntityNode;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.Effect;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class RootLayout extends BorderPane {

	// @FXML
	AnchorPane right_pane;
	// @FXML
	// ScrollPane scrollpane;

	//private DragIcon mDragOverIcon = null;
	private GraphActor graphAct;
	private EventHandler<DragEvent> mIconDragOverRoot = null;
	private EventHandler<DragEvent> mIconDragDropped = null;
	private EventHandler<DragEvent> mIconDragOverRightPane = null;
	private EventHandler<MouseEvent> mGetonMousePressed = null;
	private EventHandler<MouseEvent> mContextonMouseClick = null;
	private AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();
	private PlrActor plrAct;
	private Stage stage;
	private double x = 0, y = 0;
	private ArrayList dragNodeIds = new ArrayList();
	private int i = 0;

	private List<DraggableNode> draggableNodes = new ArrayList<>();

	public RootLayout(GraphActor gact) {

		try {
			this.graphAct = gact;

			// FXMLLoader fxmlLoader = new
			// FXMLLoader(getClass().getResource("RootLayout.fxml"));

			// fxmlLoader.setRoot(this);
			// fxmlLoader.setController(this);

			// fxmlLoader.load();

		} catch (Exception exception) {

			exception.printStackTrace();
			// throw new RuntimeException(exception);
		}

		// borderpane.setCenter(scrollpane);
		// scrollpane.setContent(right_pane);
		//right_pane.setMouseTransparent(false);
		initialize();
		// stage.setScene(new Scene(borderpane, Utils.getWindowWidth(),
		// Utils.getWindowHeight()));
	}

	// @FXML
	private void initialize() {

		// Add one icon that will be used for the drag-drop process
		// This is added as a child to the root anchorpane so it can be visible
		// on both sides of the split pane.

		// Listen to scroll events (similarly you could listen to a button click,
		// slider, ...)
		this.plrAct = AppController.plrAct;

		right_pane = new AnchorPane();
		right_pane.setPrefWidth(Utils.getWindowHeight());
		right_pane.setPrefHeight(Utils.getWindowHeight());

		//right_pane.setStyle("-fx-background-color: blue");

		//mDragOverIcon = new DragIcon();
		//mDragOverIcon.setVisible(false);
		//mDragOverIcon.setOpacity(0.65);
		//getChildren().add(mDragOverIcon);

	//	this.setPickOnBounds(false);
	//	scrollpane.setPickOnBounds(false);
		//this.setCenter(right_pane);

		//this.setContent(right_pane);

		openDraggableGraph();
		buildDragHandlers();


		right_pane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					System.out.println("mouse clickd double");
					boolean doubleClicked = event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2;
					System.out.println("event.getButton()" + event.getButton());
					System.out.println("event.getClickCount()" + event.getClickCount());
					System.out.println("doubleClicked" + doubleClicked);

					if (doubleClicked) {
						System.out.println("function starts");
						DraggableNode dragnode = new DraggableNode(graphAct, null);
						dragnode.setDisplayText("Enter text here");
						dragnode.setLayoutX(event.getSceneX());
						dragnode.setLayoutY(event.getSceneY());

						right_pane.getChildren().add(dragnode);
						event.consume();
					}
				}

			}
		});

		/*right_pane.setOnScroll((new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				double zoomFactor = 1.5;
				if (event.getDeltaY() <= 0) {
					// zoom out
					zoomFactor = 1 / zoomFactor;
				}
				zoomOperator.zoom(right_pane, zoomFactor, event.getSceneX(), event.getSceneY());
			}
		}));*/

	}





	private void buildDragHandlers() {

		// drag over transition to move widget form left pane to right pane
		mIconDragOverRoot = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

				// turn on transfer mode and track in the right-pane's context
				// if (and only if) the mouse cursor falls within the right pane's bounds.
				if (!right_pane.boundsInLocalProperty().get().contains(p)) {

					event.acceptTransferModes(TransferMode.ANY);
					//mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
					return;
				}

				event.consume();
			}
		};

		mIconDragOverRightPane = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				event.acceptTransferModes(TransferMode.ANY);

				// convert the mouse coordinates to scene coordinates,
				// then convert back to coordinates that are relative to
				// the parent of mDragIcon. Since mDragIcon is a child of the root
				// pane, coodinates must be in the root pane's coordinate system to work
				// properly.
				//mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
				event.consume();
			}
		};

		mIconDragDropped = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

				container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));

				ClipboardContent content = new ClipboardContent();
				content.put(DragContainer.AddNode, container);

				event.getDragboard().setContent(content);
				event.setDropCompleted(true);
			}
		};

		right_pane.setOnDragDone(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
				right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
				right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);

				//mDragOverIcon.setVisible(false);

				// Create node drag operation
				DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

				if (container != null) {
					if (container.getValue("scene_coords") != null) {

						if (container.getValue("type").equals(DragIconType.cubic_curve.toString())) {
							//CubicCurveDemo curve = new CubicCurveDemo();

							//right_pane.getChildren().add(curve);

							//Point2D cursorPoint = container.getValue("scene_coords");
							//System.out.println("Entered in changing position");
							//curve.relocateToPoint(new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32));
						} else {

							//DraggableNode node = new DraggableNode(graphAct, null);

							//node.setType(DragIconType.valueOf(container.getValue("type")));
							//right_pane.getChildren().add(node);

							//Point2D cursorPoint = container.getValue("scene_coords");

							//node.relocateToPoint(new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32));
						}
					}
				}

				// Move node drag operation
				container = (DragContainer) event.getDragboard().getContent(DragContainer.DragNode);
				if (container != null) {
					if (container.getValue("type") != null)
						System.out.println("Moved node " + container.getValue("type"));
				}

				// AddLink drag operation
				container = (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

				if (container != null) {

					// bind the ends of our link to the nodes whose id's are stored in the drag
					// container
					String sourceId = container.getValue("source");
					String targetId = container.getValue("target");

					if (sourceId != null && targetId != null) {

						// System.out.println(container.getData());
						NodeLink link = new NodeLink(graphAct);

						// add our link at the top of the rendering order so it's rendered first
						right_pane.getChildren().add(0, link);

						DraggableNode source = null;
						DraggableNode target = null;

						for (Node n : right_pane.getChildren()) {

							if (n.getId() == null)
								continue;

							if (n.getId().equals(sourceId))
								source = (DraggableNode) n;

							if (n.getId().equals(targetId))
								target = (DraggableNode) n;

						}

						if (source != null && target != null) {
							System.out.println("Entered in adjusting loction");
							link.bindEnds(source, target, new Point2D(event.getX(), event.getY()), link);
						}
					}

				}

				event.consume();
			}



		});

	}

	public DraggableNode addDragNode(Map<String, EntityNode> footprint, DraggableNode parent, EntityNode node) {

		DraggableNode dragnode = new DraggableNode(graphAct, node);
		System.out.println("inside adddragnode");
		if (footprint != null) {
			if (footprint.containsKey(node.getNodeId())) {
				System.out.println("node ID addDragNode" + node.getNodeId());
			} else {
				footprint.put(node.getNodeId(), node);
			}
		}
		Map<String, com.assemblogue.plr.lib.Node> node_properties = plrAct.listToMap(node);
		right_pane.getChildren().add(dragnode);
		return dragnode;
	}

	public void openDraggableGraph() {
		update_graph(true);

	}

	private void printNode(EntityNode node) {
		System.out.println("---------------------Start");
		System.out.println("node.getThumbnail" + node.getThumbnail());
		System.out.println("node.getBegin" + node.getBegin());
		System.out.println("node.getContexts" + node.getContexts());
		System.out.println("Root Node");
		// System.out.println(node.getTimelineRootNode());
		try {
			System.out.println("node.getMeta" + node.getMeta());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("node.getEnd" + node.getEnd());
		System.out.println("all property" + node.getAllProperties());
		System.out.println("node.getPeerEntity" + node.getPeerEntity());
		System.out.println("node.getRelativeURI" + node.getRelativeURI());
		try {
			System.out.println("node.getSharingUsers" + node.getSharingUsers());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("node.getURI" + node.getURI());
		System.out.println("---------------------End");
	}

	private void update_graph(boolean sync_flag) {
		System.out.println("update tree");
		draggableNodes.clear();
		Map<String, EntityNode> footprint = new LinkedHashMap<>();
		for (EntityNode node : graphAct.getEntryNodes()) {
			// printNode(node);
			System.out.println("Entered in root node iteration");
			x = 0;
			DraggableNode dragnode = createDragNode(footprint, null, node, sync_flag, true);

			// right_pane.getChildren().add(dragnode);
		}

	}

	private DraggableNode createDragNode(Map<String, EntityNode> footprint, DraggableNode parent, EntityNode node,
			boolean sync_flag, boolean isRoot) {
		// DraggableNode dragnode = null;
		boolean reentry = false;
		System.out.println("footprint" + footprint);
		System.out.println("node.getNodeId()" + node.getNodeId());
		if (footprint != null) {
			if (footprint.containsKey(node.getNodeId())) {
				reentry = true;
			} else {
				footprint.put(node.getNodeId(), node);
			}
		}

		// System.out.println(" Entered in createDragNode graphAct.isVisibleRoot(node)"
		// + graphAct.isVisibleRoot(node));
		// if (!graphAct.isVisibleRoot(node) || isRoot) {
		// DraggableNode dragnode = createDragNode(footprint, null, node, sync_flag);

		DraggableNode dragnode = new DraggableNode(graphAct, node);

		Map<String, com.assemblogue.plr.lib.Node> node_properties = plrAct.listToMap(node);
		System.out.println("node_properties" + node_properties);
		dragnode.setDisplayText(getDisplayContents(node, node_properties));
		String item_id = plrAct.getName(node);
		dragnode.ontMenu = new OntMenu(graphAct, graphAct.getOss().getNode(item_id));
		// dragnode.visibleroot = true;
		System.out.println("reentry" + reentry);
		if (!reentry) {

			right_pane.getChildren().add(dragnode);
			dragnode.setLayoutX(x);
			dragnode.setLayoutY(y);
			x = x + dragnode.getPrefWidth();

		}
		// x = 0;
		System.out.println("x" + x);
		System.out.println("y" + y);

		List<NodeInfo<com.assemblogue.plr.lib.Node>> list = graphAct.list(node);

		List<OntMenu.OntMenuItem> ranged_omi = dragnode.ontMenu.getRangedClassItem();

		List<com.assemblogue.plr.lib.Node> ni_ = node.getProperty(AppProperty.ITEM_ID_REL);
		if (ni_.size() < 1) {
			x = dragnode.getPrefWidth();
			if (!isRoot) {
				y = y + dragnode.getPrefHeight();
			}
			return dragnode;
		}
		for (com.assemblogue.plr.lib.Node n : ni_) {

			Map<String, com.assemblogue.plr.lib.Node> properties = plrAct.listToMap(n.asEntity());
			EntityNode nd = properties.get(AppProperty.RANGE).asEntity();
			System.out.println("Entered in child node iteration -create");
			// Need to change to recursive function later
			DraggableNode dragNode2 = createDragNode(footprint, null, nd, sync_flag, false);
			String value = null;
			if (!reentry) {
				if (dragnode != null && dragNode2 != null) {
					for (String key : properties.keySet()) {
						if (key.equals(AppProperty.RANGE)) {
							continue;
						}
						com.assemblogue.plr.lib.Node literal = properties.get(key);
						if (!literal.isLiteral()) {
							continue;
						}
						// サブプロパティメニューの生成
						value = OntMenu.convertNew(literal.asLiteral().getValue().toString());

						break;
					}
				}

				drawNodeLink(dragnode, dragNode2,value);

			}
		}


		return dragnode;
	}

	private void createNodeLinks() {
		ObservableList<Node> nodes = right_pane.getChildren();
		ArrayList nodeList = new ArrayList();
		for (Node node : nodes) {
			nodeList.add((DraggableNode) node);
			System.out.println("Printing Node " + node);
		}

		DraggableNode source = (DraggableNode) nodeList.get(0);
		DraggableNode target = (DraggableNode) nodeList.get(1);
		//drawNodeLink(source, target);
	}

	private void drawNodeLink(DraggableNode source, DraggableNode target, String relation) {

		NodeLink link = new NodeLink(graphAct,relation);
		right_pane.getChildren().add(link);
		link.setStart(new Point2D((source.getLayoutX() + source.getPrefWidth()),
				(source.getLayoutY() + (source.getPrefHeight() / 2))));
		link.setEnd(new Point2D(target.getLayoutX(), (target.getLayoutY() + (target.getPrefHeight() / 2))));
		link.bindEnds(source, target, new Point2D((source.getLayoutX() + source.getPrefWidth()),
				(source.getLayoutY() + (source.getPrefHeight() / 2))), link);

	}

	public DraggableNode createNodeCell() {

		DraggableNode dragnode = new DraggableNode(graphAct, null);

		return dragnode;

	}

	private String getDisplayContents(EntityNode node, Map<String, com.assemblogue.plr.lib.Node> properties) {
		if (properties == null) {
			properties = plrAct.listToMap(node);
		}

		// 決め打ち：ここで各ノードボタンの表示テキストを決める
		String val = null;
		if (properties.containsKey(AppProperty.ITEM_ID_CNT)) {
			com.assemblogue.plr.lib.Node literal = properties.get(AppProperty.ITEM_ID_CNT);
			val = literal.asLiteral().getValue().toString();
		} else {
			val = Messages.getString("nodecountents.prompt");
		}

		return val;
	}

	public AnchorPane getRightPane() {
		return this.right_pane;
	}

}