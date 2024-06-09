package com.example.roleta;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CreateRouletteFragmentTest {

    @Mock
    EditText mockEditTextTitle;

    @Mock
    EditText mockEditTextOption1;

    @Mock
    EditText mockEditTextOption2;

    @Mock
    EditText mockEditTextOption3;

    @Mock
    Button mockCreateButton;

    @Mock
    Bundle mockSavedInstanceState;
    @Mock
    ViewGroup mockContainer;
    @Mock
    LayoutInflater mockInflater;
    @Mock
    View mockView;

    @InjectMocks
    CreateRouletteFragment fragment;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);

        fragment.editTextTitle = mockEditTextTitle;
        fragment.editTextOption1 = mockEditTextOption1;
        fragment.editTextOption2 = mockEditTextOption2;
        fragment.editTextOption3 = mockEditTextOption3;
        fragment.createButton = mockCreateButton;


    }
    @Test
    public void testCreateButtonClicked() {
        fragment.createButton.performClick();
        verify(mockCreateButton).performClick();
    }

    @Test
    public void testSetOption1() {
        String expectedOption1 = "Text Option 1";
        fragment.editTextOption1.setText(expectedOption1);
        verify(mockEditTextOption1).setText(expectedOption1);
    }

    @Test
    public void testSetOption2() {
        String expectedOption2 = "Text Option 2";
        fragment.editTextOption2.setText(expectedOption2);
        verify(mockEditTextOption2).setText(expectedOption2);
    }

    @Test
    public void testSetOption3() {
        String expectedOption3 = "Text Option 3";
        fragment.editTextOption3.setText(expectedOption3);
        verify(mockEditTextOption3).setText(expectedOption3);
    }

    @Test
    public void testOnCreateView_inflatesLayoutAndFindsViews() {

        fragment = new CreateRouletteFragment();

        when(mockInflater.inflate(R.layout.fragment_create_roulette, mockContainer, false)).thenReturn(mockView);
        when(mockView.findViewById(R.id.editTextTitulo)).thenReturn(mockEditTextTitle);
        when(mockView.findViewById(R.id.editTextOpcao1)).thenReturn(mockEditTextOption1);
        when(mockView.findViewById(R.id.editTextOpcao2)).thenReturn(mockEditTextOption2);
        when(mockView.findViewById(R.id.editTextOpcao3)).thenReturn(mockEditTextOption3);
        when(mockView.findViewById(R.id.buttonCriar)).thenReturn(mockCreateButton);

        View view = fragment.onCreateView(mockInflater, mockContainer, mockSavedInstanceState);

        assertNotNull(view);
        verify(mockView).findViewById(R.id.editTextTitulo);
        verify(mockView).findViewById(R.id.editTextOpcao1);
        verify(mockView).findViewById(R.id.editTextOpcao2);
        verify(mockView).findViewById(R.id.editTextOpcao3);
        verify(mockView).findViewById(R.id.buttonCriar);
    }
}
