import {Component, ElementRef, HostListener} from '@angular/core';
import {provideTablerIcons, TablerIconComponent} from 'angular-tabler-icons';
import {
  IconAppsFilled,
  IconCategory2,
  IconCategoryFilled,
  IconLogout,
  IconMenu2,
  IconUserFilled
} from 'angular-tabler-icons/icons';
import {RouterLink} from '@angular/router';
import {UserService} from '../../../services/user.service';
import {LoadingSmallComponent} from '../../lib/loaders/loading-small/loading-small.component';

@Component({
  selector: 'app-navbar',
  imports: [
    TablerIconComponent,
    RouterLink,
    LoadingSmallComponent

  ],
  templateUrl: './navbar.component.html',
  styles: `
  `,
  providers: [
    provideTablerIcons({
      IconCategory2,
      IconUserFilled,
      IconLogout
    })
  ]
})
export class NavbarComponent {

  constructor(protected userService: UserService, private eRef: ElementRef) {

  }

  showUserPanel: boolean = false;

  toggleUserPanel(): void {
    this.showUserPanel = !this.showUserPanel;
  }

  handleLogoutButton(): void {
    this.userService.logout();
  }

  // Hostlisteners
  @HostListener('document:click', ["$event"])
  onClickOutside(event: Event): void {
    if (this.showUserPanel && !this.eRef.nativeElement.contains(event.target)) {
      this.showUserPanel = false;
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.showUserPanel = false;
  }
}
