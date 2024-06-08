package com.example.roleta;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
