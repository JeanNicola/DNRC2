/* eslint-disable max-len */
// Essentials
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/app/modules/shared/shared.module';
import { ContactsRoutingModule } from './contacts-routing.module';
import { EditComponent } from './components/edit/edit.component';
import { CreateComponent } from './components/create/create.component';
import { SearchComponent } from './components/search/search.component';
import { AddressComponent } from './components/edit/components/address/address.component';
import { WrApplsComponent } from './components/edit/components/wr-appls/wr-appls.component';
import { OwnershipUpdateComponent } from './components/edit/components/ownership-update/ownership-update.component';
import { NotTheSameComponent } from './components/edit/components/not-the-same/not-the-same.component';
import { AddressTableComponent } from './components/search/address-table/address-table.component';
import { AddressDialogComponent } from './components/search/address-dialog/address-dialog.component';
import { CreateAddressDialogComponent } from './components/create/create-address-dialog/create-address-dialog.component';
import { EditHeaderComponent } from './components/edit/components/edit-header/edit-header.component';
import { PhoneEmailComponent } from './components/edit/components/address/components/phone-email/phone-email.component';
import { WaterRightsComponent } from './components/edit/components/wr-appls/components/water-rights/water-rights.component';
import { ApplicationsComponent } from './components/edit/components/wr-appls/components/applications/applications.component';
import { NotTheSameTableComponent } from './components/edit/components/not-the-same/components/not-the-same-table/not-the-same-table.component';
import { OwnershipUpdateTableComponent } from './components/edit/components/ownership-update/components/ownership-update-actions/components/ownership-update-table/ownership-update-table.component';

// Components

@NgModule({
  declarations: [
    EditComponent,
    CreateComponent,
    SearchComponent,
    AddressComponent,
    WrApplsComponent,
    OwnershipUpdateComponent,
    NotTheSameComponent,
    AddressTableComponent,
    AddressDialogComponent,
    CreateAddressDialogComponent,
    EditHeaderComponent,
    PhoneEmailComponent,
    WaterRightsComponent,
    ApplicationsComponent,
    OwnershipUpdateTableComponent,
    NotTheSameTableComponent,
  ],
  imports: [CommonModule, ContactsRoutingModule, SharedModule],
})
export class ContactsModule {}
