/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {AfterViewInit, Directive, ElementRef, HostListener, Renderer2} from '@angular/core';

@Directive({
    selector: '[beta-tooltip]',
    standalone: false
})

export class BetaTooltipDirective implements AfterViewInit {
    tooltipConfig: any = {
        elementSelector: 'span',
        className: 'beta-tooltip',
        topClassName: 'beta-tooltip--top',
        bottomClassName: 'beta-tooltip--bottom',
        activeClassName: 'beta-tooltip--visible',
        notificationText: 'Please note this is work in progress and more functionality is coming in a future release'
    };

    tooltip: HTMLElement;

    constructor(private element: ElementRef,
                private renderer: Renderer2) {

    }

    ngAfterViewInit(): void {
        this.tooltip = this.renderer.createElement(this.tooltipConfig.elementSelector);
        this.renderer.appendChild(this.element.nativeElement, this.tooltip);

        this.renderer.addClass(this.tooltip, this.tooltipConfig.bottomClassName);
        this.renderer.addClass(this.tooltip, this.tooltipConfig.className);
        const text = this.renderer.createText(this.tooltipConfig.notificationText);
        this.renderer.appendChild(this.tooltip, text);
    }

    @HostListener('mouseenter')
    onMouseEnter(): void {
        this.renderer.addClass(this.tooltip, this.tooltipConfig.activeClassName);
    }

    @HostListener('mouseleave')
    onMouseLeave(): void {
        this.renderer.removeClass(this.tooltip, this.tooltipConfig.activeClassName);
    }

    // private get tooltipPosition(): string {
    //     let elementTop = this.element.nativeElement.getBoundingClientRect().top;
    //     let headerTop = document.querySelector('#main').getBoundingClientRect().top;
    //
    //     return elementTop - headerTop > 50 ? 'top' : 'bottom';
    // }

}
