/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.facile.mode;

import java.util.HashMap;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CMaximizeBehavior;
import bibliothek.gui.dock.common.action.predefined.CMaximizeAction;
import bibliothek.gui.dock.facile.mode.action.MaximizedModeAction;
import bibliothek.gui.dock.facile.state.MaximizeArea;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.ModeManager;
import bibliothek.gui.dock.support.mode.ModeManagerListener;
import bibliothek.gui.dock.support.util.Resources;
import bibliothek.gui.dock.util.IconManager;

/**
 * {@link Dockable}s are maximized if they take up the whole space a frame
 * or a screen offers.
 * @author Benjamin Sigg
 */
public class MaximizedMode extends AbstractLocationMode<MaximizedModeArea>{
	/** unique identifier for this mode */
	public static final Path IDENTIFIER = new Path( "dock.mode.maximized" );
	
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "maximize"-action */
    public static final String ICON_IDENTIFIER = "location.maximize";

	/** when to maximize what */
	private CMaximizeBehavior maximizeBehavior = CMaximizeBehavior.STACKED;
	
	/** the mode in which some dockable with id=key was before maximizing */
	private HashMap<String, Path> lastMaximizedMode = new HashMap<String, Path>();
	
	/** the location some dockable had before maximizing */
	private HashMap<String, Location> lastMaximizedLocation = new HashMap<String, Location>();
	
	/** the listener responsible for detecting apply-events on other modes */
	private Listener listener = new Listener();
	
	/**
	 * Creates a new mode
	 * @param control the control in whose realm this mode will work
	 */
	public MaximizedMode( CControl control ){
		setSelectModeAction( new CMaximizeAction( control ) );
	}
	
	/**
	 * Creates a new mode.
	 * @param controller the owner of this mode
	 */
	public MaximizedMode( DockController controller ){
		IconManager icons = controller.getIcons();
        icons.setIconDefault( "maximize", Resources.getIcon( "maximize" ) );
        
		setSelectModeAction( new MaximizedModeAction( controller, this ) );
	}
	
	@Override
	public void setManager( LocationModeManager manager ){
		LocationModeManager old = getManager();
		listener.replaceManager( old, manager );
		super.setManager( manager );
	}
	
	@Override
	public void add( MaximizedModeArea area ){
		super.add( area );
		area.connect( this );
	}
	
	@Override
	public MaximizedModeArea remove( String key ){
		MaximizedModeArea area = super.remove( key );
		if( area != null ){
			area.connect( null );
		}
		return area;
	}
	
	/**
	 * Sets the maximize behavior which determines what {@link Dockable} to 
	 * maximize when hitting the maximize-button.<br>
	 * Note: Changing the behavior if dockables are already shown can lead
	 * to an unspecified behavior.
	 * @param maximizeBehavior the behavior, not <code>null</code>
	 */
	public void setMaximizeBehavior( CMaximizeBehavior maximizeBehavior ){
		if( maximizeBehavior == null )
			throw new IllegalArgumentException( "maximizeBehavior must not be null" );
		this.maximizeBehavior = maximizeBehavior;
	}

	/**
	 * Gets the maximize behavior.
	 * @return the behavior, not <code>null</code>
	 * @see #setMaximizeBehavior(CMaximizeBehavior)
	 */
	public CMaximizeBehavior getMaximizeBehavior(){
		return maximizeBehavior;
	}
	
	public Path getUniqueIdentifier(){
		return IDENTIFIER;
	}

	public void runApply( Dockable dockable, Location history, AffectedSet set ){
		MaximizedModeArea area = null;
		if( history != null )
			area = get( history.getRoot() );
		if( area == null )
			area = getDefaultArea();
		
		area.prepareApply( dockable, set );
		maximize( area, dockable, set );
	}

	public Location current( Dockable dockable ){
		MaximizedModeArea area = get( dockable );
		if( area == null )
			return null;

		return new Location( area.getUniqueId(), null );
	}
	
	public boolean isCurrentMode( Dockable dockable ){
		for( MaximizedModeArea area : this ){
			if( area.isChild( dockable ) )
				return true;
		}
		return false;
	}

	public boolean isDefaultMode( Dockable dockable ){
		return false;
	}

    /**
     * Ensures that <code>dockable</code> is maximized.
     * @param area the future parent of <code>dockable</code>, can be <code>null</code>
     * @param dockable the element that should be made maximized
     * @param set a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */    
    public void maximize( MaximizedModeArea area, Dockable dockable, AffectedSet set ){
        Dockable maximizing = getMaximizingElement( dockable );
        if( maximizing != dockable )
            getManager().store( maximizing );

        if( area == null )
            area = getMaximizeArea( maximizing );

        if( area == null )
            area = getDefaultArea();

        String id = getManager().getKey( maximizing );
        LocationMode current = getManager().getCurrentMode( dockable );
        
        if( id == null && current != null ){
            lastMaximizedLocation.put( area.getUniqueId(), current.current( dockable ) );
            lastMaximizedMode.put( area.getUniqueId(), current.getUniqueIdentifier() );
        }
        else{
            lastMaximizedLocation.remove( area.getUniqueId() );
            lastMaximizedMode.remove( area.getUniqueId() );
        }

        area.setMaximized( maximizing, set );
        set.add( maximizing );
    }
	
    /**
     * Ensures that <code>dockable</code> is not maximized.
     * @param dockable the element that might be maximized currently
     * @param set a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */
    public void unmaximize( Dockable dockable, AffectedSet set ){
        MaximizedModeArea area = getMaximizeArea( dockable );
        if( area != null && area.getMaximized() != null ){
            set.add( dockable );

            dockable = area.getMaximized();
            area.setMaximized( null, set );

            String key = area.getUniqueId();
            boolean done = false;
            LocationModeManager manager = getManager();
            
            if( lastMaximizedLocation.get( key ) != null ){
            	LocationMode mode = manager.getMode( lastMaximizedMode.remove( key ) );
            	if( mode != null ){
            		done = true;
            		getManager().alter( 
            			dockable,
            			mode,
            			lastMaximizedLocation.remove( key ),
            			set );
            	}
            }
            
            if( !done ){
            	LocationMode mode = manager.getPreviousMode( dockable );
            	if( mode == null || mode == this )
            		mode = manager.getMode( NormalMode.IDENTIFIER );
                
                manager.alter( dockable, mode, set );
            }
        }
    }
    
    /**
     * Ensures that either the {@link MaximizeArea} <code>station</code> or its
     * nearest parent does not show a maximized element.
     * @param station an area or a child of an area
     * @param affected elements whose mode changes will be added to this set
     */
    public void unmaximize( DockStation station, AffectedSet affected ){
        while( station != null ){
            MaximizedModeArea area = getMaximizeArea( station );
            if( area != null ){
                Dockable dockable = area.getMaximized();
                if( dockable != null ){
                    unmaximize( dockable, affected );
                    return;
                }
            }

            Dockable dockable = station.asDockable();
            if( dockable == null )
                return;

            station = dockable.getDockParent();
        }
    }
    
    /**
     * Ensures that <code>area</code> has no maximized child.
     * @param area some area
     * @param affected the element whose mode might change
     */
    public void unmaximize( MaximizedModeArea area, AffectedSet affected ){
    	Dockable dockable = area.getMaximized();
    	if( dockable != null ){
    		unmaximize( dockable, affected );
    		return;
    	}
    }
	
    /**
     * Searches the first {@link MaximizedModeArea} which is a parent
     * of <code>dockable</code>. This method will never return
     * <code>dockable</code> itself.
     * @param dockable the element whose maximize area is searched
     * @return the area or <code>null</code>
     */
    public MaximizedModeArea getMaximizeArea( Dockable dockable ){
        DockStation parent = dockable.getDockParent();
        while( parent != null ){
        	MaximizedModeArea area = getMaximizeArea( parent );
            if( area != null )
                return area;

            dockable = parent.asDockable();
            if( dockable == null ){
                parent = null;
            }
            else{
                parent = dockable.getDockParent();
            }
        }
        return null;
    }

    /**
     * Searches the one {@link MaximizeArea} whose station is
     * <code>station</code>.
     * @param station the station whose area is searched
     * @return the area or <code>null</code> if not found
     */
    protected MaximizedModeArea getMaximizeArea( DockStation station ){
        for( MaximizedModeArea area : this ){
            if( area.isRepresenting( station ) ){
                return area;
            }
        }
        return null;
    }
    
    /**
     * Gets the element which must be maximized when the user requests that
     * <code>dockable</code> is maximized.
     * @param dockable some element, not <code>null</code>
     * @return the element that must be maximized, might be <code>dockable</code>
     * itself, not <code>null</code>
     */
    protected Dockable getMaximizingElement( Dockable dockable ){
        DockStation station = dockable.getDockParent();
        if( station == null )
            return dockable;

        if( !(station instanceof StackDockStation ))
            return dockable;

        return station.asDockable();
    }

    /**
     * Gets the element which would be maximized if <code>old</code> is currently
     * maximized, and <code>dockable</code> is or will not be maximized.
     * @param old some element
     * @param dockable some element, might be <code>old</code>
     * @return the element which would be maximized if <code>dockable</code> is
     * no longer maximized, can be <code>null</code>
     */
    protected Dockable getMaximizingElement( Dockable old, Dockable dockable ){
        if( old == dockable )
            return null;

        if( old instanceof DockStation ){
            DockStation station = (DockStation)old;
            if( station.getDockableCount() == 2 ){
                if( station.getDockable( 0 ) == dockable )
                    return station.getDockable( 1 );
                if( station.getDockable( 1 ) == dockable )
                    return station.getDockable( 0 );
            }
            if( station.getDockableCount() < 2  )
                return null;
        }

        return old;
    }

    protected void applyStarting( LocationModeEvent event ){
    	Dockable dockable = event.getDockable();
    	
		final MaximizedModeArea maxiarea = getMaximizeArea( dockable );
		if( maxiarea == null )
			return;
		
		Dockable maximizedNow = maxiarea.getMaximized();
		if( maximizedNow == null )
			return;
		
		Dockable maximized = maximizedNow == null ? null : getMaximizingElement( maximizedNow, dockable );
		
		Runnable run = maxiarea.onApply( event, maximized );
		event.setClientObject( listener, run );
    }
    
    protected void applyDone( LocationModeEvent event ){
    	Runnable run = (Runnable)event.getClientObject( listener );
    	if( run != null ){
    		run.run();
    	}
    }
    
    /**
     * A listener that adds itself to all {@link LocationMode}s a {@link LocationModeManager} has.
     * Calls to the {@link LocationMode#apply(Dockable, Location, AffectedSet) apply} method is forwarded
     * to the enclosing {@link MaximizedMode}.
     * @author Benjamin Sigg
     */
    private class Listener implements ModeManagerListener<Location, LocationMode>, LocationModeListener{
    	/**
    	 * Removes this listener from <code>oldManager</code> and adds this to <code>newManager</code>.
    	 * @param oldManager the old manager, can be <code>null</code>
    	 * @param newManager the new manager, can be <code>null</code>
    	 */
    	public void replaceManager( LocationModeManager oldManager, LocationModeManager newManager ){
    		if( oldManager != null ){
    			oldManager.removeModeManagerListener( this );
    			
    			for( LocationMode mode : oldManager.modes() ){
    				modeRemoved( oldManager, mode );
    			}
    		}
    		
    		if( newManager != null ){
    			newManager.addModeManagerListener( this );
    			
    			for( LocationMode mode : newManager.modes() ){
    				modeAdded( newManager, mode );
    			}
    		}
    	}
    	
    	public void dockableAdded( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
			// ignore
		}

		public void dockableRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
			// ignore
		}

		public void modeAdded( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
			if( mode != MaximizedMode.this ){
				mode.addLocationModeListener( this );
			}
		}

		public void modeChanged( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable, LocationMode oldMode, LocationMode newMode ){
			// ignore
		}

		public void modeRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
			mode.removeLocationModeListener( this );
		}

		public void applyDone( LocationModeEvent event ){
			MaximizedMode.this.applyDone( event );
		}

		public void applyStarting( LocationModeEvent event ){
			MaximizedMode.this.applyStarting( event );
		}
    	
    }
}
