/**
 * Copyright 2010 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addons.lazyquerycontainer.test;

import java.util.Collection;

import org.vaadin.addons.lazyquerycontainer.DefaultQueryDefinition;
import org.vaadin.addons.lazyquerycontainer.LazyQueryView;
import org.vaadin.addons.lazyquerycontainer.QueryItemStatus;

import junit.framework.TestCase;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * JUnit test for testing LazyQueryView implementation.
 * @author Tommi S.E. Laukkanen
 */
public class LazyQueryViewTest extends TestCase {

	private final int viewSize=100;
	private LazyQueryView view;
		
	protected void setUp() throws Exception {
		super.setUp();
		
		DefaultQueryDefinition definition=new DefaultQueryDefinition(this.viewSize);
		definition.addProperty("Index", Integer.class, 0, true, true);
		definition.addProperty("Reverse Index", Integer.class, 0, true, false);
		definition.addProperty("Editable", String.class, "", false, false);
		definition.addProperty(LazyQueryView.PROPERTY_ID_ITEM_STATUS, QueryItemStatus.class, QueryItemStatus.None, true, false);
		
		MockQueryFactory factory=new MockQueryFactory(viewSize,0,0);
		factory.setQueryDefinition(definition);
		view=new LazyQueryView(definition,factory);
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSize() {
		assertEquals(viewSize, view.size());		
	}
	
	public void testGetItem() {
		for(int i=0;i<viewSize;i++) {
			Item item=view.getItem(i);
			Property indexProperty=item.getItemProperty("Index");
			assertEquals(i,indexProperty.getValue());
			assertTrue(indexProperty.isReadOnly());
		}
	}
	
	public void testAscendingSort() {
		view.sort(new Object[]{"Index"},new boolean[]{true});
		
		for(int i=0;i<viewSize;i++) {
			Item item=view.getItem(i);
			Property indexProperty=item.getItemProperty("Index");
			assertEquals(i,indexProperty.getValue());
			assertTrue(indexProperty.isReadOnly());
		}
	}

	public void testDescendingSort() {
		view.sort(new Object[]{"Index"},new boolean[]{false});
		
		for(int i=0;i<viewSize;i++) {
			Item item=view.getItem(i);
			Property indexProperty=item.getItemProperty("Index");
			assertEquals(viewSize-i-1,indexProperty.getValue());
			assertTrue(indexProperty.isReadOnly());
		}
	}
	
	public void testGetSortablePropertyIds() {
		Collection<?> sortablePropertyIds=view.getDefinition().getSortablePropertyIds();
		assertEquals(1,sortablePropertyIds.size());
		assertEquals("Index",sortablePropertyIds.iterator().next());
	}
	
	public void testAddCommitItem() {
		int originalViewSize=view.size();
		assertFalse(view.isModified());
		int addIndex=view.addItem();	
		assertEquals(originalViewSize+1,view.size());
		assertEquals(QueryItemStatus.Added,
				view.getItem(addIndex).getItemProperty(LazyQueryView.PROPERTY_ID_ITEM_STATUS).getValue());
		assertTrue(view.isModified());
		view.commit();
		view.refresh();
		assertFalse(view.isModified());
		assertEquals(QueryItemStatus.None,
				view.getItem(addIndex).getItemProperty(LazyQueryView.PROPERTY_ID_ITEM_STATUS).getValue());
	}

	public void testAddDiscardItem() {
		int originalViewSize=view.size();
		assertFalse(view.isModified());
		int addIndex=view.addItem();	
		assertEquals(originalViewSize+1,view.size());
		assertEquals(QueryItemStatus.Added,
				view.getItem(addIndex).getItemProperty(LazyQueryView.PROPERTY_ID_ITEM_STATUS).getValue());
		assertTrue(view.isModified());
		view.discard();
		view.refresh();
		assertFalse(view.isModified());
		assertEquals(originalViewSize,view.size());
	}
	
	public void testModifyCommitItem() {
		int modifyIndex=0;
		assertFalse(view.isModified());
		view.getItem(modifyIndex).getItemProperty("Editable").setValue("test");
		assertTrue(view.isModified());
		view.commit();
		view.refresh();
		assertFalse(view.isModified());
		assertEquals("test",view.getItem(modifyIndex).getItemProperty("Editable").getValue());
	}

	public void testModifyDiscardItem() {
		int modifyIndex=0;
		assertFalse(view.isModified());
		view.getItem(modifyIndex).getItemProperty("Editable").setValue("test");
		assertTrue(view.isModified());
		view.discard();
		view.refresh();
		assertFalse(view.isModified());
		assertEquals("",view.getItem(modifyIndex).getItemProperty("Editable").getValue());
	}

	public void testRemoveCommitItem() {
		int removeIndex=0;
		int originalViewSize=view.size();
		assertFalse(view.isModified());
		assertFalse(view.getItem(removeIndex).getItemProperty("Editable").isReadOnly());
		view.removeItem(removeIndex);	
		assertEquals(originalViewSize,view.size());
		assertEquals(QueryItemStatus.Removed,
				view.getItem(removeIndex).getItemProperty(LazyQueryView.PROPERTY_ID_ITEM_STATUS).getValue());
		assertTrue(view.getItem(removeIndex).getItemProperty("Editable").isReadOnly());
		assertTrue(view.isModified());
		view.commit();
		view.refresh();
		assertFalse(view.isModified());
		assertEquals(originalViewSize-1,view.size());
		assertEquals(removeIndex+1,
				view.getItem(removeIndex).getItemProperty("Index").getValue());
	}	

	public void testRemoveDiscardItem() {
		int removeIndex=0;
		int originalViewSize=view.size();
		assertFalse(view.isModified());
		assertFalse(view.getItem(removeIndex).getItemProperty("Editable").isReadOnly());
		view.removeItem(removeIndex);	
		assertEquals(originalViewSize,view.size());
		assertEquals(QueryItemStatus.Removed,
				view.getItem(removeIndex).getItemProperty(LazyQueryView.PROPERTY_ID_ITEM_STATUS).getValue());
		assertTrue(view.getItem(removeIndex).getItemProperty("Editable").isReadOnly());
		assertTrue(view.isModified());
		view.discard();
		view.refresh();
		assertFalse(view.isModified());
		assertEquals(originalViewSize,view.size());
		assertEquals(removeIndex,
				view.getItem(removeIndex).getItemProperty("Index").getValue());
		assertFalse(view.getItem(removeIndex).getItemProperty("Editable").isReadOnly());
	}	
	
}
