import {
  AfterViewChecked,
  AfterViewInit,
  Component,
  ComponentFactoryResolver,
  ComponentRef,
  EventEmitter,
  OnDestroy,
  QueryList,
  Type,
  ViewChild,
  ViewChildren,
  ViewContainerRef,
} from '@angular/core';
import { Subscription } from 'rxjs';
import { MatExpansionPanel } from '@angular/material/expansion';

@Component({
  selector: 'app-edit-screen',
  templateUrl: './edit-screen.component.html',
  styleUrls: ['./edit-screen.component.scss'],
})
export class EditScreenComponent
  implements AfterViewInit, AfterViewChecked, OnDestroy
{
  @ViewChildren(MatExpansionPanel)
  public accordionInstances: QueryList<MatExpansionPanel>;

  @ViewChildren('dynamic', { read: ViewContainerRef })
  public targets: QueryList<ViewContainerRef>;

  @ViewChild('header', { read: ViewContainerRef })
  public header: ViewContainerRef;

  public error;
  public dataWasFound = false;
  public pageDefinition: EditScreenDefinition;

  private headerSubscriptions: Subscription[] = [];
  private accordionSubscriptions = [];
  protected headerComponent: ComponentRef<any>;
  protected accordionComponents = new Map();
  private initialView = true;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) {}

  public ngAfterViewInit(): void {
    // SetTimeout is used to avoid error ExpressionChangedAfterItHasBeenCheckedError
    setTimeout(() => {
      // Render Header
      // Create an instance of the Header component and assign values and event handlers as defined
      const header = this.componentFactoryResolver.resolveComponentFactory(
        this.pageDefinition.header.component
      );

      this.headerComponent = this.header.createComponent(header);

      // Set input properties for the Header component
      this._setInputs(
        this.headerComponent.instance,
        this.pageDefinition.header.properties
      );

      // Set event handlers for the Header component
      this.headerSubscriptions = this._setOutputs(
        this.headerComponent.instance,
        this.pageDefinition.header.events
      );
    }, 0);
  }

  public ngAfterViewChecked(): void {
    // If this is the first time through, and the accordions exist, open the accordions
    // that are initially expanded
    if (this.initialView && this.accordionInstances.length > 0) {
      this.initialView = false;
      setTimeout(() => {
        this.pageDefinition.accordions.forEach(
          (a: EditScreenAccordionDefinition, idx: number) => {
            if (a.expanded !== undefined && a.expanded) {
              if (a?.disabled !== true) {
                this.accordionInstances.get(idx).open();
              }
            }
          }
        );
      }, 0);
    }
  }

  // Open the accordion and instantiate the child component
  public openedAccordion(index: number): void {
    // If no accordions exists yet, exit since nothing can happen
    if (this.targets.length === 0) {
      return;
    }

    setTimeout(() => {
      // Get the specific accordion child that was opened
      const target = this.targets.get(index);

      // Create an instance of the accordion component and assign values and event handlers as defined
      const component = this.componentFactoryResolver.resolveComponentFactory(
        this.pageDefinition.accordions[index].component
      );

      const componentRef: ComponentRef<any> = target.createComponent(component);

      // Set event properties for the corresponding component
      this._setInputs(
        componentRef.instance,
        this.pageDefinition.accordions[index].properties
      );

      // Set event handlers for the Header component
      this.accordionSubscriptions[index] = this._setOutputs(
        componentRef.instance,
        this.pageDefinition.accordions[index].events
      );

      componentRef.changeDetectorRef.detectChanges();
      this.accordionComponents.set(index, componentRef);
    }, 0);
  }

  // Close the accordion and remove the child component and subscriptions
  public closedAccordion(index: number): void {
    // If no accordions exists yet, exit since nothing can happen
    if (this.targets.length === 0) {
      return;
    }

    // Get the specific accordion child that was opened and clear any existing view
    const target = this.targets.get(index);
    target.clear();

    // Clear any subscriptions
    if (this.accordionSubscriptions[index]) {
      this.accordionSubscriptions[index].forEach((sub: Subscription) => {
        sub.unsubscribe();
      });
    }

    // Remove component from list
    this.accordionComponents.delete(index);
  }

  public ngOnDestroy(): void {
    // Clear out header component and subscriptions
    if (this.headerComponent) {
      this.headerComponent.destroy();
    }

    this.headerSubscriptions.forEach((sub: Subscription) => {
      sub.unsubscribe();
    });

    // Clear out accordion components and subscriptions
    for (const comp of this.accordionComponents.values()) {
      (comp as ComponentRef<any>).destroy();
    }

    this.accordionSubscriptions.forEach((subList: Subscription[]) => {
      subList.forEach((sub: Subscription) => {
        sub.unsubscribe();
      });
    });
  }

  // Set the input properties on the given component
  private _setInputs(comp: ComponentRef<any>, properties: any): void {
    if (properties === undefined) {
      return;
    }

    // Set input properties for the component
    Object.keys(properties).forEach((key) => {
      // If the @Input property does not exist on the component, throw an error.
      // This ensures the definition matches the actual component
      if (!(key in comp)) {
        throw new ReferenceError(
          `@Input property '${key}' on component ${comp.constructor.name} does not exist`
        );
      }

      comp[key] = properties[key];
    });
  }

  // Set the output properties / event handlers on the given component
  // Return an array of subscrptions so they can be unsubscribed later
  private _setOutputs(comp: ComponentRef<any>, outputs: any): Subscription[] {
    const subs: Subscription[] = [];
    if (outputs === undefined) {
      return subs;
    }

    Object.keys(outputs).forEach((key) => {
      // If the @Output property does not exist on the component, throw an error.
      // This ensures the definition matches the actual component
      if (!(key in comp)) {
        throw new ReferenceError(
          `@Output property '${key}' on component ${comp.constructor.name} does not exist`
        );
      }

      // If the property does exist but is not an EventEmitter, throw an error.
      // This prevents later trying to subscribe to an incorrect property type
      if (!(comp[key] instanceof EventEmitter)) {
        throw new Error(
          `@Output property '${key}' on component ${comp.constructor.name} is not an EventEmitter`
        );
      }

      // If the event property is not a function, then throw an error. Events must be handled by functions
      if (!(typeof outputs[key] === 'function')) {
        throw new TypeError(
          `Event Handler '${key}' in Header definition on component ${this.constructor.name} is not a function`
        );
      }

      // Subscribe to the event on the component
      const sub: Subscription = comp[key].subscribe((data) => {
        outputs[key](data);
      });

      // Save the subscription so it can be unsubscribed during onDestroy event
      subs.push(sub);
    });

    return subs;
  }

  // Refresh any open accordions with new data
  protected refresh(): void {
    this.accordionInstances.forEach((a: MatExpansionPanel, idx: number) => {
      if (a.expanded) {
        const comp: ComponentRef<any> = this.accordionComponents.get(
          idx
        ) as ComponentRef<any>;
        this._setInputs(
          comp.instance,
          this.pageDefinition.accordions[idx].properties
        );
        comp.changeDetectorRef.detectChanges();
      }
    });
  }
}

export interface EditScreenDefinition {
  header?: {
    component: Type<any>;
    properties?: {
      title?: string;
      [key: string]: any;
    };
    events?: {
      [key: string]: any;
    };
  };
  accordions: EditScreenAccordionDefinition[];
}

export interface EditScreenAccordionDefinition {
  component: Type<any>;
  title?: string;
  expanded?: true | false;
  disabled?: true | false;
  condensed?: true | false;
  properties?: {
    [key: string]: any;
  };
  events?: {
    [key: string]: any;
  };
  onParentData?: (
    accordionIndex: number,
    thisAccordion: EditScreenAccordionDefinition,
    data: any,
    ...opts: any[]
  ) => void;
}
